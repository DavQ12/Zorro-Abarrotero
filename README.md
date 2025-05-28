# Zorro-Abarrotero
Proyecto final Spring

## Cosas a mejorar.
### Insertar el correo del cliente en la parte de seleccionar productos cajera (VentasZorro)
![img_1.png](img_1.png)
### actualizar la cantidad de productos en la bd despuesd q se genere la venta.

### este proyecto esta por el puerto 8082
### cambiar config del correo
### cambiar la ruta de la imagenes(creas nueva carpeta), mi ruta es de linux
### para ingresar al sistema deberas generar el hash de la pwd encriptada e ingresarla desde mysql o mariadb segun el usuario

//metodo para genera el hash
public static void main(String[] args) {
      System.out.println(new BCryptPasswordEncoder().encode("123456")); 
}

y luego con update o insert ingresas la cadena junto con el correo y usuario rol
genera dos uno cajera y admin
