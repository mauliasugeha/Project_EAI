# EAI Final Project: Microservices Order Management System

Proyek ini adalah implementasi sistem *Order Management* berbasis *Microservices Architecture* menggunakan pola **Saga (Choreography-based)** dengan **Apache Kafka** sebagai *message broker*. Sistem ini dirancang untuk menangani alur pemesanan secara *event-driven* yang tangguh dan terdesentralisasi.

## 🏗️ Arsitektur Sistem
Sistem terdiri dari beberapa layanan independen:
* **Order Management Service**: Mengelola *lifecycle* pesanan.
* **Restaurant Service**: Mengelola inventaris dan validasi stok.
* **Payment Service**: Memproses pembayaran.
* **Shipping Service**: Mengelola pengiriman barang.
* **Notification Service**: Mengirimkan notifikasi email ke pelanggan.

## 🚀 Fitur Utama
* **Saga Pattern**: Menjamin konsistensi data di seluruh *microservices* melalui *event-driven communication*.
* **Event-Driven Communication**: Menggunakan Apache Kafka untuk sinkronisasi antar-servis yang *decoupled*.
* **Resilience**: Penanganan kegagalan secara otomatis dengan *fallback* dan pembatalan status (cancel/rollback).
* **Security**: Integrasi OAuth2 & JWT dengan Keycloak melalui API Gateway.
* **Modern Stack**: Spring Boot 4, Kafka, MySQL, dan RESTful API.

## 🛠️ Tech Stack
* **Language**: Java 17+
* **Framework**: Spring Boot 4.0.x, Spring Cloud Gateway, Spring Kafka
* **Message Broker**: Apache Kafka
* **Database**: MySQL
* **Build Tool**: Maven

## 👥 Contributor
Proyek ini dikembangkan oleh tim beranggotakan 3 orang:
1. **Maulia Dwi Anthesa Sugeha**
2. **Irene Noer Ramadhany**
3. **Latifa Anggia Fitriana**

## 📦 Struktur Repositori
* `order-management/`: Core order dan sinkronisasi status.
* `payment-service/`: Integrasi alur pembayaran.
* `restaurant-service/`: Manajemen stok dan validasi produk.
* `shipping-service/`: Logistik dan pembaruan pengiriman.
* `notification-service/`: Notifikasi email berbasis event.

## ⚙️ Cara Menjalankan
1. Pastikan **Apache Kafka** dan **Zookeeper** sudah berjalan di `localhost:9092`.
2. Pastikan **MySQL** sudah berjalan dan buat database sesuai konfigurasi di `application.properties` masing-masing servis.
3. Jalankan masing-masing *service* dengan perintah:
   ```bash
   ./mvnw spring-boot:run
