
package com.productos.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "tb_factura")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Factura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fa_id")
    private Integer id;

    @Column(name = "fa_fecha", nullable = false)
    private LocalDate fecha;

    @Column(name = "fa_total", nullable = false)
    private Double total;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @OneToMany(mappedBy = "factura")
    private List<DetalleFactura> detalles;
}
