package zorroAbarrotes.proyecto.service.venta;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zorroAbarrotes.proyecto.model.entity.CarritoEntity;
import zorroAbarrotes.proyecto.model.entity.ProductoCarritoEntity;
import zorroAbarrotes.proyecto.model.entity.VentaEntity;
import zorroAbarrotes.proyecto.repository.VentaRepository;
import zorroAbarrotes.proyecto.service.carrito.CarritoService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class VentaServiceImpl implements VentaService {

    private final VentaRepository ventaRepository;
    private final CarritoService carritoService;

    @Autowired
    public VentaServiceImpl(VentaRepository ventaRepository, CarritoService carritoService) {
        this.ventaRepository = ventaRepository;
        this.carritoService = carritoService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<VentaEntity> findByCarrito(CarritoEntity carrito) {
        return ventaRepository.findByCarrito(carrito);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VentaEntity> findAll() {
        List<VentaEntity> ventas = ventaRepository.findAll();
        if (ventas != null) {
            for (VentaEntity venta : ventas) {
                if (venta.getUsuario() != null) {
                    Hibernate.initialize(venta.getUsuario());
                }
                if (venta.getPago() != null) {
                    Hibernate.initialize(venta.getPago());
                }
                if (venta.getCarrito() != null) {
                    Hibernate.initialize(venta.getCarrito());
                }
            }
        }
        return ventas;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VentaEntity> findAllWithDetails(Pageable pageable) {
        Page<VentaEntity> ventas = ventaRepository.findAllWithDetailsAndPagination(pageable);
        if (ventas != null && ventas.getContent() != null) {
            for (VentaEntity venta : ventas.getContent()) {
                if (venta.getUsuario() != null) {
                    Hibernate.initialize(venta.getUsuario());
                }
                if (venta.getPago() != null) {
                    Hibernate.initialize(venta.getPago());
                }
                if (venta.getCarrito() != null) {
                    Hibernate.initialize(venta.getCarrito());
                }
                if (venta.getCliente() != null) {
                    Hibernate.initialize(venta.getCliente());
                }
            }
        }
        return ventas;
    }

    @Override
    public Optional<VentaEntity> findByIdWithAllDetails(Long id) {
        return ventaRepository.findByIdWithAllDetails(id);
    }

    @Override
    @Transactional
    public VentaEntity save(VentaEntity venta) {
        try {
            // Verificar que las entidades relacionadas no sean null
            if (venta.getPago() == null ||
                    venta.getCarrito() == null || venta.getUsuario() == null) {
                throw new RuntimeException("Faltan datos requeridos para la venta");
            }

            // Asegurar que las entidades relacionadas tienen IDs válidos
            if (venta.getUsuario().getId() == null) {
                throw new RuntimeException("El usuario no tiene ID");
            }
            if (venta.getPago().getId() == null) {
                throw new RuntimeException("El pago no tiene ID");
            }
            if (venta.getCarrito().getId() == null) {
                throw new RuntimeException("El carrito no tiene ID");
            }

            // Establecer la fecha de venta si no está establecida
            if (venta.getFechaVenta() == null) {
                venta.setFechaVenta(LocalDateTime.now());
            }

            // Guardar la venta
            VentaEntity savedVenta = ventaRepository.save(venta);
            if (savedVenta == null) {
                throw new RuntimeException("Error al guardar la venta. Datos: " +
                        "Usuario ID: " + venta.getUsuario().getId() +
                        ", Pago ID: " + venta.getPago().getId() +
                        ", Carrito ID: " + venta.getCarrito().getId());
            }

            // Verificar que se generó un ID
            if (savedVenta.getId() == null) {
                throw new RuntimeException("No se generó ID para la venta. Datos: " +
                        "Usuario ID: " + venta.getUsuario().getId() +
                        ", Pago ID: " + venta.getPago().getId() +
                        ", Carrito ID: " + venta.getCarrito().getId());
            }

            // Inicializar todas las relaciones
            if (savedVenta.getCarrito() != null) {
                Hibernate.initialize(savedVenta.getCarrito());
                if (savedVenta.getCarrito().getProductosCarrito() != null) {
                    for (ProductoCarritoEntity productoCarrito : savedVenta.getCarrito().getProductosCarrito()) {
                        Hibernate.initialize(productoCarrito);
                        if (productoCarrito.getProducto() != null) {
                            Hibernate.initialize(productoCarrito.getProducto());
                        }
                    }
                }
            }
            if (savedVenta.getUsuario() != null) {
                Hibernate.initialize(savedVenta.getUsuario());
            }
            if (savedVenta.getPago() != null) {
                Hibernate.initialize(savedVenta.getPago());
            }
            if (savedVenta.getCliente() != null) {
                Hibernate.initialize(savedVenta.getCliente());
            }

            return savedVenta;
        } catch (Exception e) {
            throw new RuntimeException("Error al guardar la venta: " + e.getMessage(), e);
        }
    }


//    @Override
//    public VentaEntity save(VentaEntity actor) {
//        return ventaRepository.save(actor);
//    }

    @Override
    @Transactional(readOnly = true)
    public List<VentaEntity> findAllWithDetails() {
        List<VentaEntity> ventas = ventaRepository.findAllWithDetailsList();
        if (ventas != null) {
            for (VentaEntity venta : ventas) {
                if (venta.getUsuario() != null) {
                    Hibernate.initialize(venta.getUsuario());
                }
                if (venta.getPago() != null) {
                    Hibernate.initialize(venta.getPago());
                }
                if (venta.getCarrito() != null) {
                    Hibernate.initialize(venta.getCarrito());
                }
                if (venta.getCliente() != null) {
                    Hibernate.initialize(venta.getCliente());
                }
            }
        }
        return ventas;
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Optional<VentaEntity> venta = findById(id);
        if (venta.isPresent()) {
            // Eliminar la venta
            ventaRepository.delete(venta.get());
        }
    }

//    @Override
//    public void deleteById(Long id) {
//        ventaRepository.deleteById(id);
//    }

    @Override
    @Transactional
    public void deleteByIdWithCascade(Long id) {
        Optional<VentaEntity> venta = findById(id);
        if (venta.isPresent()) {
            VentaEntity ventaEntity = venta.get();
            CarritoEntity carrito = ventaEntity.getCarrito();

            // First, delete the venta (this will break the foreign key constraint)
            ventaRepository.deleteByIdWithCascade(id);

            // Then delete the carrito and its related entries
            if (carrito != null) {
                // Delete all producto_carrito entries
                carritoService.deleteProductoCarritoByCarritoId(carrito.getId());

                // Then delete the carrito itself
                carritoService.deleteById(carrito.getId());
            }
        }
    }

    @Override
    public Long countAll() {
        return ventaRepository.countAll();
    }

    public Long countAllNative() {
        return ventaRepository.countAllNative();
    }

    @Override
    public Optional<VentaEntity> findById(Long id) {
        Optional<VentaEntity> actor = ventaRepository.findById(id);
        return ventaRepository.findById(id);
    }
}
