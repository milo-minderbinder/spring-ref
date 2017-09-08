package co.insecurity.springref.persistence.domain;

import co.insecurity.springref.core.domain.UserRole;
import co.insecurity.springref.event.users.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Entity
@Table(name = "USERS")
public class User implements UserDetails, Serializable {

    private static final Logger LOG =
            LoggerFactory.getLogger(User.class);
    private static final long serialVersionUID = 7832409291921802512L;


    @Id
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private boolean enabled;

    @ElementCollection(targetClass = UserRole.class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<UserRole> roles;

    private String firstName;
    private String lastName;
    private String street;
    private String city;
    private String state;
    private int zip;
    private String email;

    public User() {
        this(null, null);
        LOG.debug("New user created with default constructor");
    }

    public User(String username, String password) {
        this(username, password, true, new HashSet<UserRole>());
        LOG.debug("New user: {}", this.toString());
    }

    public User(String username, String password, boolean enabled, Set<UserRole> roles) {
        this.username = username;
        this.password = password;
        this.enabled = enabled;
        this.roles = roles;
        LOG.debug("New user: {}", this.toString());
    }

    public static User fromUserInfo(UserInfo userInfo) {
        LOG.debug("Creating new User from UserInfo: {}", userInfo.toString());
        User user = new User();
        BeanUtils.copyProperties(userInfo, user);
        LOG.debug("New user created from UserInfo: {}", user.toString());
        return user;
    }

    public UserInfo toUserInfo() {
        UserInfo userInfo = new UserInfo();
        BeanUtils.copyProperties(this, userInfo);
        return userInfo;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Set<UserRole> getRoles() {
        return roles;
    }

    public void setRoles(Set<UserRole> roles) {
        this.roles = roles;
    }

    public void addRole(UserRole userRole) throws IllegalArgumentException {
        if (userRole == null)
            throw new IllegalArgumentException("Cannot add null UserRole with addRole()");
        else
            roles.add(userRole);
    }

    @Override
    public List<GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        for (UserRole userRole : roles)
            authorities.add(userRole.toSimpleGrantedAuthority());
        if (authorities.size() == 0)
            authorities.add(UserRole.ANONYMOUS.toSimpleGrantedAuthority());
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getZip() {
        return zip;
    }

    public void setZip(int zip) {
        this.zip = zip;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return String.format("User"
                        + "[username=%s, "
                        + "enabled=%b, "
                        + "authorities=%s, "
                        + "firstName='%s', "
                        + "lastName='%s']",
                username, enabled, getAuthorities().toString(), firstName, lastName);
    }

    /**
     * Returns {@code true} if the supplied object is a {@code User} instance
     * with the same {@code username} value.
     * <p>
     * In other words, the objects are equal if they have the same username,
     * representing the same principal.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof User) {
            return username.equals(((User) obj).username);
        }
        return false;
    }

    /**
     * Returns the hashcode of the {@code username}.
     */
    @Override
    public int hashCode() {
        return username.hashCode();
    }
}