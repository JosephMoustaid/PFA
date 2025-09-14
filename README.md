# PFA (Projet de Fin d’Année)

This repo is for my **PFA (Projet de Fin d’Année)** — my 4th-year final project.

---

## Table of Contents

1. [About](#about)  
2. [Technologies](#technologies)  
3. [Features](#features)  
4. [Setup & Installation](#setup--installation)  
5. [Usage](#usage)  
6. [API Reference](#api-reference)  
7. [Contributing](#contributing)  
8. [License](#license)  

---

## About

“PFA” is a backend API for *Bricole*. It implements all the backend features, integrates an NLP model (for recommendations), and serves as the server that a frontend/chat-app client can talk to.

It also provides resources for:

- Running/testing the API routes  
- Running a separate NLP server (Flask) for recommendation logic  
- Providing a React example client for chat interactions  

---

## Technologies

Here are the main technologies used in this project:

| Component | Technology |
|-----------|------------|
| Backend API | ![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white) ![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white) |
| NLP Server | ![Python](https://img.shields.io/badge/Python-3776AB?style=for-the-badge&logo=python&logoColor=white) ![Flask](https://img.shields.io/badge/Flask-000000?style=for-the-badge&logo=flask&logoColor=white) |
| Frontend / Example Chat | ![React](https://img.shields.io/badge/React-20232A?style=for-the-badge&logo=react&logoColor=61DAFB) |
| Build / Dependency Management | ![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white) |
| Other | `.mvn` wrapper, etc. |

---

## Features

- Full backend API supporting all required endpoints for *Bricole*  
- NLP-based recommendation model served separately (Flask server)  
- Example usage via a React client to demonstrate the chat API  

---

## Setup & Installation

Follow these steps to get the project up and running locally:

1. Clone the repo:

   ```bash
   git clone https://github.com/JosephMoustaid/PFA.git
   cd PFA
