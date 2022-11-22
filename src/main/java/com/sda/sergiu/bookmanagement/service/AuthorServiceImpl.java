package com.sda.sergiu.bookmanagement.service;

import com.sda.sergiu.bookmanagement.model.Author;
import com.sda.sergiu.bookmanagement.repository.AuthorRepository;
import com.sda.sergiu.bookmanagement.service.exception.EntityNotFoundException;
import com.sda.sergiu.bookmanagement.service.exception.InvalidParameterException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;

    public AuthorServiceImpl(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @Override
    public void createAuthor(String firstName, String lastName) throws InvalidParameterException {
        if (firstName == null || firstName.isBlank() || firstName.length() < 3) {
            throw new InvalidParameterException("Provided value for first name: " + firstName + " is invalid!");
        }
        if (lastName == null || lastName.isBlank() || lastName.length() < 3) {
            throw new InvalidParameterException("Provided value for last name: " + lastName + " is invalid!");
        }

        authorRepository.create(new Author(firstName, lastName));
    }

    @Override
    public void updateAuthor(int authorId, String firstName, String lastName) throws InvalidParameterException, EntityNotFoundException {
        if (authorId < 1) {
            throw new InvalidParameterException("Provided value for author id: " + authorId + " is invalid!");
        }
        if (firstName == null || firstName.isBlank() || firstName.length() < 3) {
            throw new InvalidParameterException("Provided value for first name: " + firstName + " is invalid!");
        }
        if (lastName == null || lastName.isBlank() || lastName.length() < 3) {
            throw new InvalidParameterException("Provided value for last name: " + lastName + " is invalid!");
        }
        Optional<Author> authorOptional = authorRepository.findById(authorId);
        if (authorOptional.isEmpty()) {
            throw new EntityNotFoundException("Author with id: " + authorId + "was not found!");
        }
        Author author = authorOptional.get();
        author.setFirstname(firstName);
        author.setLastname(lastName);
        authorRepository.update(author);
    }

    @Override
    public void deleteAuthor(int authorId) throws InvalidParameterException, EntityNotFoundException {
        if (authorId < 1) {
            throw new InvalidParameterException("Provided value for author id: " + authorId + " is invalid!");
        }
        Optional<Author> authorOptional = authorRepository.findById(authorId);
        if (authorOptional.isEmpty()) {
            throw new EntityNotFoundException("Author with id: " + authorId + " was not found!");
        }
        Author author = authorOptional.get();
        authorRepository.delete(author);
    }

    @Override
    public void importAuthors() throws IOException {
        Path filepath = Paths.get("C:\\Users\\User\\Documents\\book-management-system-sda\\src\\main\\resources\\Data\\Authors.csv");
        Files.lines(filepath)
                .skip(1)
                .filter(line -> line != null)
                .filter(line -> !line.isBlank())
                .map(line -> line.split("\\|"))
                .forEach(authorProperties -> {
                    //Primary Key is autogenerated
                    //int authorId = Integer.parseInt(authorProperties[0]);
                    try {
                        String firstName = authorProperties[1];
                        String lastName = authorProperties[2];
                        createAuthor(firstName, lastName);
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                    }
                });
    }

    @Override
    public List<Author> getAllAuthors() {
        return authorRepository.findAll();
    }
}
