

# E-Commerce Backend Application



## Deskripsi Proyek

Proyek ini adalah backend untuk aplikasi e-commerce yang dibangun menggunakan Spring Boot. Backend ini menyediakan berbagai fitur untuk mengelola produk, pesanan, dan pengguna dalam sebuah platform e-commerce.

## Fitur Utama

- Manajemen pengguna: Registrasi, login, dan verifikasi pengguna.
- Manajemen produk: Menambahkan, menghapus, dan mengupdate produk.
- Manajemen pesanan: Menampilkan dan mengelola pesanan pengguna.

## Teknologi Utama

- **Spring Boot**: Framework Java yang digunakan untuk membangun aplikasi backend.
- **Spring Security**: Untuk keamanan dan otentikasi pengguna.
- **Spring Data JPA**: Untuk interaksi dengan database menggunakan Hibernate.
- **JWT (JSON Web Token)**: Untuk autentikasi pengguna.
- **JUnit 5**: Untuk pengujian unit.
- **MockMvc**: Untuk pengujian integrasi.

## Struktur Proyek

```
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── Backend/
│   │   │           └── SpringBoot/
│   │   │               └── E_Commerce_backend/
│   │   │                   ├── api/
│   │   │                   │   ├── controller/
│   │   │                   │   │   ├── AuthenticationController.java
│   │   │                   │   │   ├── OrderController.java
│   │   │                   │   │   ├── ProductController.java
│   │   │                   │   │   └── UserController.java
│   │   │                   │   ├── model/
│   │   │                   │   │   ├── LocalUser.java
│   │   │                   │   │   ├── WebOrder.java
│   │   │                   │   │   └── Product.java
│   │   │                   │   └── security/
│   │   │                   │       ├── JUnitUserDetailsService.java
│   │   │                   │       ├── JWTRequestFilter.java
│   │   │                   │       └── JWTServices.java
│   │   │                   ├── model/
│   │   │                   │   ├── dao/
│   │   │                   │   │   ├── LocalUserDAO.java
│   │   │                   │   │   ├── OrderDAO.java
│   │   │                   │   │   └── ProductDAO.java
│   │   │                   │   ├── VerificationToken.java
│   │   │                   │   └── EncryptionServices.java
│   │   │                   └── services/
│   │   │                       ├── UserService.java
│   │   │                       ├── ProductService.java
│   │   │                       ├── OrderService.java
│   │   │                       └── JWTServices.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/
│           └── com/
│               └── Backend/
│                   └── SpringBoot/
│                       └── E_Commerce_backend/
│                           ├── api/
│                           │   └── controller/
│                           │       ├── AuthenticationControllerTest.java
│                           │       ├── OrderControllerTest.java
│                           │       ├── ProductControllerTest.java
│                           │       └── UserControllerTest.java
│                           ├── model/
│                           │   └── service/
│                           │       ├── EncryptionServiceTest.java
│                           │       └── JWTServiceTest.java
│                           └── service/
│                               └── UserServiceTest.java
└── pom.xml
```

## Instalasi

1. **Clone repository ini:**

   ```
   git clone https://github.com/hendrowunga/E-Commerce-backend-SpringBoot.git
   cd repository
   ```

2. **Build dan jalankan aplikasi:**

   ```
   mvn clean install
   mvn spring-boot:run
   ```

3. **Akses aplikasi:**

   Buka `http://localhost:8080` di web browser.

## Penggunaan Endpoint
- POST : http://localhost:8080/auth/register
- POST : http://localhost:8080/auth/login
- GET  : http://localhost:8080/order
- GET  : http://localhost:8080/product
- GET  : http://localhost:8080/user/{userId}/address
- PUT  : http://localhost:8080/user/{userId}/address
- PATCH: http://localhost:8080/user/{userId}/address/{addressId}

### 1. AuthenticationController

#### AuthenticationController.java

```java
@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private JWTServices jwtServices;

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody LocalUser newUser) {
        userService.saveUser(newUser);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LocalUser loginUser) {
        // Authenticate user
        final String jwtToken = jwtServices.authenticate(loginUser.getUsername(), loginUser.getPassword());
        return ResponseEntity.ok(new AuthResponse(jwtToken));
    }
}
```

### 2. OrderController

#### OrderController.java

```java
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    public ResponseEntity<List<WebOrder>> getAllOrders() {
        List<WebOrder> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }
}
```

### 3. ProductController

#### ProductController.java

```java
@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }
}
```

### 4. UserController

#### UserController.java 

```java

@RestController
@RequestMapping("/user")
public class UserController {
    private AddressDAO addressDAO;
    private SimpMessagingTemplate simpMessagingTemplate;
    private UserServices userServices;

    public UserController(AddressDAO addressDAO,SimpMessagingTemplate simpMessagingTemplate,UserServices userServices) {
        this.addressDAO = addressDAO;
        this.simpMessagingTemplate=simpMessagingTemplate;
        this.userServices=userServices;
    }

    @GetMapping("/{userId}/address")
    public ResponseEntity<List<Address>> getAddress(@AuthenticationPrincipal LocalUser user, @PathVariable Long userId) {
        if (!userServices.userHasPermissionToUser(user, userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(addressDAO.findByUser_Id(userId));
    }


    @PutMapping("/{userId}/address")
    public ResponseEntity<Address> putAddress(@AuthenticationPrincipal LocalUser user, @PathVariable Long userId, @RequestBody Address address) {
        if (!userServices.userHasPermissionToUser(user, userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        address.setId(null);
        LocalUser refUser = new LocalUser();
        refUser.setId(userId);
        address.setUser(refUser);
        Address savedAddress = addressDAO.save(address);
        simpMessagingTemplate.convertAndSend("/topic/user/" + userId + "/address",
                new DataChange<>(DataChange.ChangeType.INSERT, address));
        return ResponseEntity.ok(savedAddress);
    }

    @PatchMapping("/{userId}/address/{addressId}")
    public ResponseEntity<Address> patchAddress(
            @AuthenticationPrincipal LocalUser user, @PathVariable Long userId,
            @PathVariable Long addressId, @RequestBody Address address) {
        if (!userServices.userHasPermissionToUser(user, userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        if (address.getId() == addressId) {
            Optional<Address> opOriginalAddress = addressDAO.findById(addressId);
            if (opOriginalAddress.isPresent()) {
                LocalUser originalUser = opOriginalAddress.get().getUser();
                if (originalUser.getId() == userId) {
                    address.setUser(originalUser);
                    Address savedAddress = addressDAO.save(address);
                    simpMessagingTemplate.convertAndSend("/topic/user/" + userId + "/address",
                            new DataChange<>(DataChange.ChangeType.UPDATE, address));
                    return ResponseEntity.ok(savedAddress);
                }
            }
        }
        return ResponseEntity.badRequest().build();
    }


}
```
## Dokumentasi dan Referensi
- Spring Boot Documentation: https://docs.spring.io/spring-boot/docs/current/reference/html/
- Start Spring Boot: https://start.spring.io/
- Stack Overflow: https://stackoverflow.com/



## Hubungi Saya

Untuk pertanyaan lebih lanjut atau diskusi, silakan hubungi saya melalui email di **hendrowunga073@gmail.com.com**.



