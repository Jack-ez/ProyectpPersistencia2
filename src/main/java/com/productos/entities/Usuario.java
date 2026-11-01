package com.productos.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "tb_usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "us_id")
    private Integer id;

    @Column(name = "us_nombre", nullable = false)
    private String nombre;

    @Column(name = "us_rol", nullable = false)
    private String rol;

    @Column(name = "us_usuario", nullable = false)
    private String usuario;

    @Column(name = "us_password", nullable = false)
    private String password;

    @OneToMany(mappedBy = "usuario")
    private List<Cliente> clientes;
}
