## Proyecto de Métodos de Ingeniería de Software (2024)

### Aplicación Web: Cálculo y Visualización de Planillas de Sueldos

Este proyecto es una aplicación web para el cálculo y visualización de planillas de sueldos en la empresa ficticia de fabricación de muebles "Muebles Stgo".

### Funcionalidades
La aplicación calcula los sueldos considerando:
- **Sueldo base** según la categoría del empleado.
- **Descuentos** por atrasos y ausencias.
- **Bonos** por horas extras y años de servicio.
- **Descuentos legales** por jubilación y salud.

Además permite:
- Justificar ausencias
- Autorizar horas extras
- Crear, editar y eliminar empleados
- Ver planilla completa de empleados
- Ver planillas de sueldo por mes y año

### Entrada de Datos
La información de los registros de reloj se ingresa desde un archivo `DATA.txt`, con el siguiente formato:

```txt
2024/08/05;08:00;11.234.123-6
2024/08/05;07:58;12.457.562-3
```

### Instrucciones

1) Primero, ingrese información del reloj desde un archivo 'DATA.txt'.
2) Una vez ingresada la información, puede calcular la planilla de sueldos para el año y mes ingresado anteriormente.
3) Al terminar el cálculo podrá ver un reporte con la planilla.
4) Si ya ha realizado el cálculo para algún mes, podrá ver las planillas en la sección "Ver Planillas de sueldo" y filtrar por año y mes.