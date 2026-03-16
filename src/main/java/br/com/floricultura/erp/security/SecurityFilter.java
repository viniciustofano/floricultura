package br.com.floricultura.erp.security;

import br.com.floricultura.erp.repository.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired private TokenService tokenService;
    @Autowired private UsuarioRepository usuarioRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String uri = request.getRequestURI();
        String method = request.getMethod();
        var token = recoverToken(request);

        if (token == null) {
            System.out.println("🔒 [AUTH] Requisição sem token: " + method + " " + uri);
        } else {
            var login = tokenService.validateToken(token);
            if (login == null) {
                System.out.println("❌ [AUTH] Token inválido ou expirado na rota: " + method + " " + uri);
            } else {
                UserDetails user = usuarioRepository.findByEmail(login).orElse(null);
                if (user != null) {
                    var auth = new UsernamePasswordAuthenticationToken(
                            user, null, user.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    System.out.println("✅ [AUTH] Usuário autenticado: " + login + " → " + method + " " + uri);
                } else {
                    System.out.println("❌ [AUTH] Usuário do token não encontrado no banco: " + login);
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return null;
        return authHeader.replace("Bearer ", "");
    }
}