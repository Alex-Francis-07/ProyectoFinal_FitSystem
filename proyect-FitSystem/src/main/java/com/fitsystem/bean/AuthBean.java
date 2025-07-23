package com.fitsystem.bean;

import com.fitsystem.model.Usuario;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import java.io.Serializable;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.servlet.http.HttpSession;
import java.util.List;

@Named("authBean")
@SessionScoped
public class AuthBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private String username;
    private String password;
    private Usuario usuarioAutenticado;

    @PersistenceContext(unitName = "fitsystemPU")
    private transient EntityManager em;

    @Transactional
    public String login() {
        TypedQuery<Usuario> query = em.createQuery(
            "SELECT u FROM Usuario u WHERE u.username = :username AND u.password = :password",
            Usuario.class);
        query.setParameter("username", username);
        query.setParameter("password", password);

        List<Usuario> resultados = query.getResultList();

        System.out.println("DEBUG AuthBean.login(): encontrados " 
            + resultados.size() 
            + " usuario(s) para username='" 
            + username 
            + "': " 
            + resultados);

        if (!resultados.isEmpty()) {
            usuarioAutenticado = resultados.get(0);

            
            HttpSession session = (HttpSession)
                FacesContext.getCurrentInstance()
                            .getExternalContext()
                            .getSession(true);
            session.setAttribute("authBean", this);

            return "/views/dashboard.xhtml?faces-redirect=true";
        } else {
            FacesContext.getCurrentInstance()
                .addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                     "Usuario o contraseña incorrectos",
                                     null));
            return null;
        }
    }

    public String logout() {
        // Limpia la sesión
        HttpSession session = (HttpSession)
            FacesContext.getCurrentInstance()
                        .getExternalContext()
                        .getSession(false);
        if (session != null) {
            session.invalidate();
        }

        usuarioAutenticado = null;
        username = null;
        password = null;
        return "/login.xhtml?faces-redirect=true";
    }

    // Getters y setters

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Usuario getUsuarioAutenticado() {
        return usuarioAutenticado;
    }

    public boolean isLogueado() {
        return usuarioAutenticado != null;
    }
}
