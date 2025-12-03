# Image to PDF API

This project is a Spring Boot application that allows users to upload images and convert them into a PDF file. It utilizes Apache ORC for efficient data handling and processing.

## Features

- Upload images via a RESTful API.
- Convert uploaded images into a single PDF document.
- Simple and intuitive interface for image processing.

## Technologies Used

- Java
- Spring Boot
- Apache ORC
- Maven

## Project Structure

```
image-to-pdf-api
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │       └── example
│   │   │           ├── Application.java
│   │   │           ├── controller
│   │   │           │   └── ImageController.java
│   │   │           ├── service
│   │   │           │   ├── ImageService.java
│   │   │           │   └── PdfService.java
│   │   │           └── model
│   │   │               └── ImageUploadRequest.java
│   │   └── resources
│   │       └── application.properties
│   └── test
│       └── java
│           └── com
│               └── example
│                   └── service
│                       └── PdfServiceTest.java
├── pom.xml
└── README.md
```

## Setup Instructions

1. Clone the repository:
   ```
   git clone <repository-url>
   ```

2. Navigate to the project directory:
   ```
   cd image-to-pdf-api
   ```

3. Build the project using Maven:
   ```
   mvn clean install
   ```

4. Run the application:
   ```
   mvn spring-boot:run
   ```

## Usage

- To upload an image, send a POST request to `/api/images/upload` with the image file included in the request body.
- The API will process the image and generate a PDF file, which can be downloaded or accessed as specified in the response.

## Contributing

Contributions are welcome! Please open an issue or submit a pull request for any enhancements or bug fixes.

## License

This project is licensed under the MIT License.