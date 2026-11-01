
package com.productos.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tb_detalle_factura")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DetalleFactura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "df_id")
    private Integer id;

    @Column(name = "df_cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "df_precio_unitario", nullable = false)
    private Double precioUnitario;

    @Column(name = "df_subtotal", nullable = false)
    private Double subtotal;

    @ManyToOne
    @JoinColumn(name = "factura_id", nullable = false)
    private Factura factura;

    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;
}

