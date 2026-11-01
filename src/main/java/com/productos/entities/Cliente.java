

package com.productos.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "tb_cliente")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cl_id")
    private Integer id;

    @Column(name = "cl_nombre", nullable = false)
    private String nombre;

    @Column(name = "cl_telefono")
    private String telefono;

    @Column(name = "cl_direccion")
    private String direccion;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @OneToMany(mappedBy = "cliente")
    private List<Factura> facturas;
}
