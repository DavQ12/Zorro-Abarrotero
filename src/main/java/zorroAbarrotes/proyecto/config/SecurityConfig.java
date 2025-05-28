package zorroAbarrotes.proyecto.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import zorroAbarrotes.proyecto.service.security.UserDetailsServiceImp;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private CustomSuccessHandler customSuccessHandler;


    @Bean
    //http es objeto
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorize ->
                        authorize
                                //.requestMatchers("/", "/login", "/registro", "/test-auth", "/usuarios/**", "/css/**", "/js/**", "/images/**", "/iconos/**", "/bootstrap/**", "/clientes/registro").permitAll()
                                .requestMatchers("/static/**", "/image/**","/css/**", "/js/**", "/images/**", "/iconos/**", "/bootstrap/**").permitAll()
                                .requestMatchers("/admin", "/admin/**", "/productos", "/productos/**", "/proveedores", "/productos/**", "/usuarios", "/usuarios/**").hasRole("administrador")
                                .anyRequest().authenticated()
                )
                .formLogin(form ->
                        form
                                .loginPage("/")
                                .loginProcessingUrl("/")
                                .successHandler(customSuccessHandler)
                                .failureUrl("/?error=true")
                                .permitAll()
                )
                .logout(logout ->
                        logout
                                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                                .logoutSuccessUrl("/?logout=true")
                                .invalidateHttpSession(true)
                                .clearAuthentication(true)
                                .permitAll()
                );

        return httpSecurity.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsServiceImp userDetailsService) throws Exception {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }



    @Bean
    public static PasswordEncoder passwordEncoder() {
        //Encriptado
        return new BCryptPasswordEncoder();
    }

//    public static void main(String[] args) {
//        System.out.println(new BCryptPasswordEncoder().encode("123456"));
//    }

}
