
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
````

2. Backend setup (Java):

   * Make sure you have Java SDK & Maven installed.
   * Run `mvn clean install` to build.
   * Use the `.mvn` wrapper to ease setup if you prefer.

3. NLP model server:

   * Go to the related repo: `allMini-LLM-l6-v2--flask-server-for-NLP-processing`.
   * Install dependencies (e.g. with `pip install -r requirements.txt`).
   * Run the NLP server (`python main.py`) — it should listen on port 5000.

4. Example React chat client (demo):

   * Visit the React example repo: `React-Chat-API-Test`.
   * Install its dependencies (`npm install` / `yarn`).
   * Run it to connect to your API + NLP server to see chat functionality.

---

## Usage

Here are some example usage flows:

* Call any of the API endpoints to manage data (depending on what the project supports: create, read, update, delete, etc.).
* Send requests through the React client demo to test chat and recommendation functionality.
* Use the NLP server separately to generate recommendations; endpoints should redirect or call it as required by the backend.

---

## API Reference

*(You should fill this in with your actual endpoints, parameters, request/response formats. Here's a starter example.)*

| Endpoint         | Method | Description                         |
| ---------------- | ------ | ----------------------------------- |
| `/api/login`     | POST   | Authenticate user                   |
| `/api/items`     | GET    | Get list of items                   |
| `/api/items/:id` | GET    | Get a single item by ID             |
| `/api/recommend` | POST   | Get recommendations via NLP service |

> ⚠️ Be sure to update with actual routes and schemas.

---

## Contributing

If you want to contribute:

1. Fork the repo
2. Create a new branch (`git checkout -b feature/YourFeatureName`)
3. Make your changes & tests
4. Commit, push, and open a Pull Request

---

## License

This project is licensed under the *\[Your License Choice]*. (e.g. MIT, Apache 2.0, etc.)

---

## Contact

* **Author:** Joseph Moustaid
* **GitHub:** [@JosephMoustaid](https://github.com/JosephMoustaid)

