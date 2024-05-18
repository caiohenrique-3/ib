# Simple Board

Simple Board is a forum-like website where users can post images/text, and engage in discussions with each other by creating and replying to threads. This project is built using Spring Boot, Thymeleaf, Hibernate, and MariaDB.

## Preview

https://github.com/caiohenrique-3/simple-textboard/assets/104094613/68fdabf2-3bbb-4f6d-8449-10d92d86c386

## Running on your machine

Before running the project, make sure you have MariaDB installed and correctly set up on your machine. You will need to configure the `src/main/resources/application.properties` file with your MariaDB database information.

1. Clone the repository: `git clone https://github.com/caiohenrique-3/simple-board.git`
2. Navigate to the project directory: `cd simple-board`
3. Install the dependencies: `mvn install -DskipTests=true`
4. Start the server: `mvn spring-boot:run`

Once the server is running, navigate to `http://localhost:8080` in your web browser.

## Contributing

To contribute to this project, follow these steps:

1. Fork the repository
2. Create a new branch (`git checkout -b feature`)
3. Make your changes and commit them (`git commit -m 'Add new feature'`)
4. Push to the branch (`git push origin feature`)
5. Open a pull request

## Issues

If you encounter any issues or have suggestions for improvements, please
[open an issue](https://github.com/caiohenrique-3/simple-board/issues).

## License

This project is licensed under the [MIT License](LICENSE).

---
