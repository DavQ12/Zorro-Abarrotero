package zorroAbarrotes.proyecto.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import zorroAbarrotes.proyecto.model.entity.CarritoEntity;
import zorroAbarrotes.proyecto.model.entity.VentaEntity;


import java.util.List;
import java.util.Optional;

public interface VentaRepository extends JpaRepository<VentaEntity, Long> {

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM venta WHERE id_venta = :id", nativeQuery = true)
    void deleteByIdWithCascade(@Param("id") Long id);
    @Query("SELECT v FROM VentaEntity v " +
            "LEFT JOIN FETCH v.usuario " +
            "LEFT JOIN FETCH v.pago " +
            "LEFT JOIN FETCH v.carrito " +
            "LEFT JOIN FETCH v.cliente")
    List<VentaEntity> findAllWithDetailsList();

    @Query("SELECT v FROM VentaEntity v " +
            "JOIN FETCH v.carrito c " +
            "JOIN FETCH c.productosCarrito pc " +
            "JOIN FETCH pc.producto p " +
            "LEFT JOIN FETCH v.usuario u " +
            "LEFT JOIN FETCH v.pago pg " +
            "LEFT JOIN FETCH v.cliente cl " +
            "WHERE v.id = :id")
    Optional<VentaEntity> findByIdWithAllDetails(@Param("id") Long id);

    @Query("SELECT v FROM VentaEntity v " +
            "LEFT JOIN FETCH v.usuario u " +
            "LEFT JOIN FETCH v.pago p " +
            "LEFT JOIN FETCH v.carrito c " +
            "LEFT JOIN FETCH v.cliente cl " +
            "WHERE v.id IS NOT NULL")
    Page<VentaEntity> findAllWithDetailsAndPagination(Pageable pageable);

    @Query("SELECT v FROM VentaEntity v " +
            "LEFT JOIN FETCH v.usuario " +
            "LEFT JOIN FETCH v.pago " +
            "LEFT JOIN FETCH v.carrito " +
            "LEFT JOIN FETCH v.cliente " +
            "WHERE v.id = ?1")
    Optional<VentaEntity> findByIdWithDetails(Long id);

    List<VentaEntity> findByCarrito(CarritoEntity carrito);

    @Query("SELECT COUNT(v) FROM VentaEntity v WHERE v.id IS NOT NULL")
    Long countAll();

    @Query(value = "SELECT COUNT(*) FROM venta", nativeQuery = true)
    Long countAllNative();
}
