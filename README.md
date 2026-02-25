# primer-parcial-seminario
Cada ViewModel expone un StateFlow que la UI observa con collectAsState()
HabitsViewModel y BudgetViewModel son completamente independientes entre sí

## Integración de Hábitos y Finanzas
La integración ocurre en el DashboardViewModel, que usa el operador combine() para escuchar tres fuentes de datos al mismo tiempo: la lista de hábitos, las completaciones del día, y las transacciones del mes. Cuando cualquiera cambia, el Dashboard se recalcula automáticamente mostrando métricas de ambos módulos juntos.
Se colocó el porcentaje de los hábitos completado y el porcentaje del presupuesto

## Flujo de la app:
La app arranca en MainActivity, que aplica el tema y lanza la navegación. Hay una barra de navegación inferior con 4 secciones: Dashboard, Hábitos, Presupuesto y Perfil.

Link de los screenshots: https://drive.google.com/drive/folders/1-Vtnh8Gm6PBFd6vvhjmRcXzM5qlI3EiX?usp=sharing
