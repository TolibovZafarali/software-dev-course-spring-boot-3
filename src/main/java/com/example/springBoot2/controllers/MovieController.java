package com.example.springBoot2.controllers;

import com.example.springBoot2.Repositories.MovieRepository;
import com.example.springBoot2.models.Movie;
import com.example.springBoot2.models.Movie;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/movies")
public class MovieController {

    private final MovieRepository movieRepository;

    public MovieController(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @GetMapping("")
    public String renderMoviesHomePage() {
        List<Movie> allMovies = movieRepository.findAll();
        StringBuilder moviesList = new StringBuilder();
        for (Movie movie : allMovies) {
            moviesList.append("<li><a href='/movies/details/").append(movie.getId()).append("'>").append(movie.getName()).append(" - ").append(movie.getDirector()).append("</a></li>");
        }
        return """
                <html>
                <body>
                <h2>MOVIES</h2>
                <ul>
                """ +
                moviesList +
                """
                </ul>
                <p><a href='/movies/add'>Add</a> another movie or <a href='/movies/delete'>delete</a> one or more movies.</p>
                </body>
                </html>
                """;
    }

    @GetMapping("/details/{movieId}")
    public String displayMovieDetails(@PathVariable(value="movieId") int movieId) {
        Movie currentMovie = movieRepository.findById(movieId).orElse(null);
        if (currentMovie != null) {
            return """
                    <html>
                    <body>
                    <h3>Movie Details</h3>
                    """ +
                    "<p><b>ID:</b> " + movieId + "</p>" +
                    "<p><b>Name:</b> " + currentMovie.getName() + "</p>" +
                    "<p><b>Director:</b> " + currentMovie.getDirector() + "</p>" +
                    "<p><b>Year:</b> " + currentMovie.getYear() + "</p>" +
                    "<p><b>Runtime:</b> " + currentMovie.getRuntime() + "</p>" +
                    "<p><a href='/movies/update/" + currentMovie.getId() + "'>Update</a></p>" +
                    """
                    </body>
                    </html>
                    """;
        } else {
            return """
                    <html>
                    <body>
                    <h3>Movie Details</h3>
                    <p>Movie not found. <a href='/movies'>Return to list of movies.</a></p>
                    </body>
                    </html>
                    """;
        }
    }

    @GetMapping("/add")
    public String renderAddMovieForm() {
        return """
                <html>
                <body>
                <form action='/movies/add' method='POST'>
                <p>Enter the details of a movie:</p>
                <input type='text' name='name' placeholder='Name' />
                <input type='text' name='director' placeholder='Director' />
                <input type='number' name='year' placeholder='Year' />
                <input type='number' name='runtime' placeholder='Runtime' />
                <button type='submit'>Add</button>
                </form>
                </body>
                </html>
                """;
    }

    @PostMapping("/add")
    public String processAddMovieForm(@RequestParam(value="name") String name, @RequestParam(value="director") String director, @RequestParam(value="year") int year, @RequestParam(value="runtime") int runtime) {
        Movie newMovie = new Movie(name, director, year, runtime);
        movieRepository.save(newMovie);
        return """
                <html>
                <body>
                <h3>MOVIE ADDED</h3>
                """ +
                "<p>You have successfully added " + name + " to the collection.</p>" +
                """
                <p><a href='/movies/add'>Add</a> another movie or view the <a href='/movies'>updated list</a> of movies.</p>
                </body>
                </html>
                """;
    }

    @GetMapping("/update/{movieId}")
    public String updateMovieDetails(@PathVariable(value = "movieId") int movieId) {
        Movie currentMovie = movieRepository.findById(movieId).orElse(null);
        if (currentMovie != null) {
            return """
                    <html>
                    <body>
                    <h3>Update Movie Details</h3>
                    """ +
                    "<form action='/movies/update/" + currentMovie.getId() + "' method='POST'>" +
                    "<p>Update the details of a movie:</p>" +
                    "<input type='text' name='name' value='" + currentMovie.getName() + "' placeholder='Name' />" +
                    "<input type='text' name='director' value='" + currentMovie.getDirector() + "' placeholder='Director' />" +
                    "<input type='number' name='year' value='" + currentMovie.getYear() + "' placeholder='Year' />" +
                    "<input type='number' name='runtime' value='" + currentMovie.getRuntime() + "' placeholder='Runtime' />" +
                    "<button type='submit'>Update</button>" +
                    """
                    </body>
                    </html>
                    """;
        } else {
            return """
                    <html>
                    <body>
                    <h3>Update Movie</h3>
                    <p>Movie not found. <a href='/movies'>Return to list of movies.</a></p>
                    </body>
                    </html>
                    """;
        }
    }

    @PostMapping("/update/{movieId}")
    public String processUpdateMovieDetails(@PathVariable(value = "movieId") int movieId, @RequestParam(value = "name") String name, @RequestParam(value = "director") String director, @RequestParam(value = "year") int year, @RequestParam(value = "runtime") int runtime) {
        Movie movieToUpdate = movieRepository.findById(movieId).orElse(null);

        if (movieToUpdate != null) {
            movieToUpdate.setName(name);
            movieToUpdate.setDirector(director);
            movieToUpdate.setYear(year);
            movieToUpdate.setRuntime(runtime);

            movieRepository.save(movieToUpdate);

            return """
                <html>
                <body>
                <h3>MOVIE UPDATED</h3>
                """ +
                    "<p>You have successfully updated " + name + " to the collection.</p>" +
                    """
                    <p>View the <a href='/movies'>updated list</a> of movies.</p>
                    </body>
                    </html>
                    """;
        } else {
            return """
                    <html>
                    <body>
                    <h3>Update Movie</h3>
                    <p>Movie not found. <a href='/movies'>Return to list of movies.</a></p>
                    </body>
                    </html>
                    """;
        }
    }

    @GetMapping("/delete")
    public String renderDeleteMovieForm() {
        List<Movie> allMovies = movieRepository.findAll();
        StringBuilder moviesList = new StringBuilder();
        for (Movie movie : allMovies) {
            int currId = movie.getId();
            moviesList.append("<li><input id='").append(currId).append("' name='movieIds' type='checkbox' value='").append(currId).append("' />").append(movie.getName()).append(" - ").append(movie.getDirector()).append("</li>");
        }
        return """
                <html>
                <body>
                <form action='/movies/delete' method='POST'>
                <p>Select which movies you wish to delete:</p>
                <ul>
                """ +
                moviesList +
                """
                </ul>
                <button type='submit'>Submit</button>
                </form>
                </body>
                </html>
                """;
    }

    @PostMapping("/delete")
    public String ProcessDeleteMovieForm(@RequestParam(value="movieIds") int[] movieIds) {
        for (int id : movieIds) {
            Movie currMovie = movieRepository.findById(id).orElse(null);
            if (currMovie != null) {
                movieRepository.deleteById(id);

            }
        }
        String header = movieIds.length > 1 ? "MOVIES" : "MOVIE";
        return """
                <html>
                <body>
                <h3>
                """ +
                header +
                """
                DELETED</h3>
                <p>Deletion successful.</p>
                <p>View the <a href='/movies'>updated list</a> of movies.</p>
                </body>
                </html>
                """;
    }
}
