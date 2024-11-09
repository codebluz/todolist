package br.com.brunoluz.todolist.filter;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.brunoluz.todolist.user.IUserRepository;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {

  @Autowired
  private IUserRepository userRepository;

  @Override
  protected void doFilterInternal(HttpServletRequest request,
      HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {

    var servletPath = request.getServletPath();

    if (servletPath.startsWith("/tasks")) {
      var auth = request.getHeader("Authorization");

      auth = auth.replace("Basic", "").trim();

      String decoded = new String(Base64.getDecoder().decode(auth));

      String[] credentials = decoded.split(":");
      String username = credentials[0];
      String password = credentials[1];

      var user = this.userRepository.findByUsername(username);

      if (user == null) {
        response.sendError(401, "Unauthorized User");
      } else {

        var passwordMatches = BCrypt.verifyer()
            .verify(password.toCharArray(), user.getPassword());

        if (!passwordMatches.verified) {
          response.sendError(401, "Wrong Credentials");
        } else {
          request.setAttribute("user_id", user.getId());
          filterChain.doFilter(request, response);
        }
      }
    } else {
      filterChain.doFilter(request, response);
    }
  }
}
