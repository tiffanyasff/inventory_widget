# Documentación de Implementación de Dagger Hilt

Se ha implementado **Dagger Hilt** para la inyección de dependencias en el proyecto `inventory_widget`. A continuación se detallan los cambios realizados y la estructura de la inyección.

## 1. Configuración Inicial
- Se añadió la clase `InventoryApplication` anotada con `@HiltAndroidApp`.
- Se actualizó el `AndroidManifest.xml` para usar esta nueva clase `Application`.
- Se configuraron las dependencias de Hilt en `build.gradle.kts` y `libs.versions.toml`.

## 2. Módulo de Dependencias (`AppModule`)
Se creó el archivo `app/src/main/java/com/univalle/inventorywidget/di/AppModule.kt` para proveer instancias globales (Singleton):
- **Base de Datos (Room):** `InventoryDB` y `InventoryDao`.
- **Firebase:** `FirebaseFirestore` y `FirebaseAuth`.

## 3. Repositorios
Los repositorios fueron refactorizados para recibir sus dependencias vía constructor (`@Inject`):
- `InventoryRepository`: Recibe `InventoryDao` y `FirebaseFirestore`.
- `LoginRepository`: Recibe `FirebaseAuth`.
- `AddItemRepository`: Recibe `FirebaseFirestore`.
- `ListItemRepository`: Recibe `FirebaseFirestore`.

## 4. ViewModels
Se migraron a `@HiltViewModel`, eliminando la necesidad de `ViewModelFactory` manuales:
- `InventoryViewModel`: Inyecta `InventoryRepository` y `LoginRepository`.
- `AddItemViewModel`: Inyecta `AddItemRepository`.
- `LoginViewModel`: Inyecta `LoginRepository`.
- `ListItemViewModel`: Inyecta `InventoryRepository`.

## 5. Vistas (Activity / Fragment / Widget)
Se anotaron con `@AndroidEntryPoint` para permitir la inyección de dependencias:
- **Activities:** `MainActivity`, `LoginActivity`.
- **Fragments:** `AddItemFragment`, `HomeInventoryFragment`, `ItemDetailsFragment`, `ItemEditFragment`.
- **Widget:** `InventoryWidget`.

En las vistas, la instanciación de ViewModels cambió de:
```kotlin
private val viewModel: MyViewModel by viewModels { MyFactory(...) }
```
a:
```kotlin
private val viewModel: MyViewModel by viewModels()
```

## 6. InventoryWidget
El Widget ahora inyecta `InventoryRepository` directamente en lugar de instanciarlo manualmente.

## 7. Actualización de Versiones
Para garantizar la compatibilidad entre Hilt, KSP y Kotlin (debido a problemas con metadatos de versiones recientes), se realizaron las siguientes actualizaciones en `build.gradle.kts` y `libs.versions.toml`:
- **Kotlin:** Actualizado a `2.1.0`.
- **KSP:** Actualizado a `2.1.0-1.0.29`.
- **Hilt:** Actualizado a `2.55`.

## 8. Próximos Pasos (Recomendado)
- Eliminar las clases `AddItemViewModelFactory` y `LoginViewModelFactory` ya que no son necesarias (Ya eliminadas).
- Ejecutar `./gradlew clean build` para asegurar que todo el código generado por Hilt es correcto.
