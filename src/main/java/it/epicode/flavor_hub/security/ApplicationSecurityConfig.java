package it.epicode.flavor_hub.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Properties;

@Configuration
//QUESTA ANNOTAZIONE SERVE A COMUNICARE A SPRING CHE QUESTA  CLASSE Ã¨ UTILIZZATA PER CONFIGURARE LA SECURITY
@EnableWebSecurity()
@EnableMethodSecurity(prePostEnabled = true)
public class ApplicationSecurityConfig {

    @Bean
    PasswordEncoder stdPasswordEncoder() {
        return new BCryptPasswordEncoder(11);
    }

    @Bean
    AuthTokenFilter authenticationJwtToken() {
        return new AuthTokenFilter();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http,
                                                       PasswordEncoder passwordEncoder,
                                                       UserDetailsService userDetailsService) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);

        authenticationManagerBuilder
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder);

        return authenticationManagerBuilder.build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults()) // Utilizza la configurazione CORS
                .authorizeHttpRequests(authorize ->
                                authorize //CONFIGURAZIONE DELLA PROTEZIONE DEI VARI ENDPOINT
                                        .requestMatchers("/users/login").permitAll()
                                        .requestMatchers("/users/registerAdmin").permitAll() // DA CANCELLARE DOPO AVER CREATO L'ADMIN
                                        .requestMatchers(HttpMethod.PUT, "/users/{id}").authenticated() //SOLO UN UTENTE AUTENTICATO PUO MODIFICARE I SUOI DATI
                                        .requestMatchers(HttpMethod.DELETE, "/users/{id}").authenticated() //SOLO UN UTENTE AUTENTICATO PUO MODIFICARE I SUOI DATI
                                        .requestMatchers(HttpMethod.GET, "/recipes/**").permitAll()
                                        .requestMatchers(HttpMethod.PUT, "/recipes/{id}").authenticated() //SOLO UN UTENTE AUTENTICATO PUO MODIFICARE I SUOI DATI
                                        .requestMatchers(HttpMethod.DELETE, "/recipes/{id}").authenticated() //SOLO UN UTENTE AUTENTICATO PUO MODIFICARE I SUOI DATI
                                        .requestMatchers(HttpMethod.POST, "/users").permitAll() //ENDPOINT DI REGISTRAZIONE APERTO A TUTTI
                                        .requestMatchers(HttpMethod.GET, "/**").authenticated()
                                        .requestMatchers(HttpMethod.PUT, "/**").hasAuthority("ADMIN") //TUTTE LE PUT POSSONO ESSERE FATTE SOLO DALL'ADMIN
                                        .requestMatchers(HttpMethod.DELETE, "/**").hasAuthority("ADMIN") //TUTTE LE DELETE POSSONO ESSERE FATTE SOLO DALL'ADMIN
                                        .requestMatchers(HttpMethod.PATCH, "/users/{id}").authenticated() //SOLO UN UTENTE AUTENTICATO PUO MODIFICARE I SUOI DATI
                                        .requestMatchers(HttpMethod.POST, "/users/like").authenticated()
                                        .requestMatchers(HttpMethod.DELETE, "/users/like").authenticated()//SOLO UN UTENTE AUTENTICATO PUO MODIFICARE I SUOI DATI
                                        .requestMatchers(HttpMethod.POST, "/recipes").authenticated()
                        //.requestMatchers("/**").authenticated() //TUTTO CIO CHE PUO ESSERE SFUGGITO RICHIEDE L'AUTENTICAZIONE (SERVE A GESTIRE EVENTUALI DIMENTICANZE)
                )
                .httpBasic(Customizer.withDefaults())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                //COMUNICA ALLA FILTERCHAIN QUALE FILTRO UTILIZZARE, SENZA QUESTA RIGA DI CODICE IL FILTRO NON VIENE RICHIAMATO
                .addFilterBefore(authenticationJwtToken(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public JavaMailSenderImpl getJavaMailSender(@Value("${spring.mail.host}") String host,
                                            @Value("${spring.mail.port}") int port,
                                            @Value("${spring.mail.username}") String username,
                                            @Value("${spring.mail.password}") String password,
                                            @Value("${spring.mail.properties.mail.smtp.auth}") String auth,
                                            @Value("${spring.mail.properties.mail.smtp.starttls.enable}") String starttls,
                                            @Value("${spring.mail.properties.mail.smtp.connectiontimeout}") String connectionTimeout,
                                            @Value("${spring.mail.properties.mail.smtp.timeout}") String timeout,
                                            @Value("${spring.mail.properties.mail.smtp.writetimeout}") String writeTimeout) {

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setUsername(username);
        mailSender.setPassword(password);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.smtp.auth", auth);
        props.put("mail.smtp.starttls.enable", starttls);
        props.put("mail.smtp.connectiontimeout", connectionTimeout);
        props.put("mail.smtp.timeout", timeout);
        props.put("mail.smtp.writetimeout", writeTimeout);

        return mailSender;
    }

}

