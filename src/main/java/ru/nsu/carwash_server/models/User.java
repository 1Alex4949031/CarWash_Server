package ru.nsu.carwash_server.models;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "users")
@NoArgsConstructor
@ToString
@Getter @Setter
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    @Column(nullable = false, unique = true)
    private String username;
    @NotBlank
    private String phone;
    @OneToMany(mappedBy = "user")
    @ToString.Exclude
    private Set<Order> orders;
    @Email
    @Column(unique = true)
    private String email;
    private int bonuses;
    @NotBlank
    @ToString.Exclude
    @Column(nullable = false, unique = true)
    private String password;
    @OneToMany(mappedBy = "user")
    @ToString.Exclude
    private Set<Auto> auto;
    private String fullName;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    @ToString.Exclude
    private Set<Role> roles = new HashSet<>();
    public User(Long id) {
        this.id = id;
    }
    public User(String username, String password) {
        this.username = username;
        this.password = password;
        setBonuses(100);
        setPhone(username);
    }
}
