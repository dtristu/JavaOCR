# Java OCR

Java OCR is a web application designed to automate Optical Character Recognition (OCR) workflows. The application extracts text from various document formats, making it easy to process, store, and retrieve text from files like PDFs and DOCX. Built with a robust, event-driven microservices architecture, Java OCR ensures efficient handling of documents, from initial input to text extraction and storage. 

## Features

- **Automated OCR Workflow**: Processes documents using Tesseract OCR for accurate text extraction.
- **Microservices Architecture**: Built with a microservices approach using Spring Boot, ensuring modularity and scalability.
- **Event-Driven Communication**: Utilizes Kafka for seamless communication between services.
- **Document Processing**: Supports processing of PDF and DOCX files with Apache PDFBox and docx4j.
- **Secure Access Control**: Enforced with Spring Security and cookie-based authorization.
- **API Management**: Spring Gateway for efficient API routing and request handling.
- **Scalable Data Storage**: Uses MongoDB for document-oriented storage, accommodating diverse data formats and scaling as needed.
- **Robust Testing**: Ensures functionality and reliability with comprehensive unit tests using JUnit 5.

## Technologies Used

- **Spring Boot**: Microservices development
- **Kafka**: Event-driven communication
- **Tesseract OCR**: Optical Character Recognition
- **MongoDB**: Document-oriented data storage
- **Apache PDFBox**: PDF document processing
- **docx4j**: DOCX document processing
- **Spring Security**: Authorization and access control
- **Spring Gateway**: API management and routing
- **JUnit 5**: Unit testing for reliability

## Architecture

Java OCR is built with a microservices architecture where each component performs a distinct function. Kafka serves as the message broker for inter-service communication, ensuring the application remains responsive and efficient even under high loads.

The main services include:
- **Document Processing Service**: Handles document ingestion, transformation, and OCR processing.
- **Storage Service**: Manages the storage and retrieval of processed data.
- **API Gateway**: Routes requests to the appropriate services and enforces security.
- **Authentication Service**: Manages user authentication and authorization.
  
## Usage
- **Upload Documents**: Use the provided API to upload PDF or DOCX documents.
- **Retrieve Extracted Text**: Once processed, retrieve the extracted text through API endpoints.
- **Manage Users and Access**: The application supports user management through secure, cookie-based authentication.
- **Example API endpoints**:
- **POST /documents**: Upload a new document for OCR processing.
- **GET /documents/{id}**: Retrieve processed text for a specific document.

## Prerequisites

- **Java 11 or later**
- **Apache Kafka**
- **MongoDB**
- **Tesseract OCR**
- **Docker (optional)** for containerized deployment

