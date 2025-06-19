package com.example.springBoot2.controllers;

import com.example.springBoot2.Repositories.AlbumRepository;
import com.example.springBoot2.models.Album;
import com.example.springBoot2.models.Album;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/albums")
public class AlbumController {

    private final AlbumRepository albumRepository;

    public AlbumController(AlbumRepository albumRepository) {
        this.albumRepository = albumRepository;
    }

    @GetMapping("")
    public String renderAlbumsHomePage() {
        List<Album> allAlbums = albumRepository.findAll();
        StringBuilder albumsList = new StringBuilder();
        for (Album album : allAlbums) {
            albumsList.append("<li><a href='/albums/details/").append(album.getId()).append("'>").append(album.getName()).append(" - ").append(album.getArtist()).append("</a></li>");
        }
        return """
                <html>
                <body>
                <h2>ALBUMS</h2>
                <ul>
                """ +
                albumsList +
                """
                </ul>
                <p><a href='/albums/add'>Add</a> another album or <a href='/albums/delete'>delete</a> one or more albums.</p>
                </body>
                </html>
                """;
    }

    @GetMapping("/details/{albumId}")
    public String displayAlbumDetails(@PathVariable(value="albumId") int albumId) {
        Album currentAlbum = albumRepository.findById(albumId).orElse(null);
        if (currentAlbum != null) {
            return """
                    <html>
                    <body>
                    <h3>Album Details</h3>
                    """ +
                    "<p><b>ID:</b> " + albumId + "</p>" +
                    "<p><b>Name:</b> " + currentAlbum.getName() + "</p>" +
                    "<p><b>Artist:</b> " + currentAlbum.getArtist() + "</p>" +
                    "<p><b>Year:</b> " + currentAlbum.getYear() + "</p>" +
                    "<p><b>Tracks:</b> " + currentAlbum.getTracks() + "</p>" +
                    "<p><a href='/albums/update/" + currentAlbum.getId() + "'>Update</a></p>" +
                    """
                    </body>
                    </html>
                    """;
        } else {
            return """
                    <html>
                    <body>
                    <h3>Album Details</h3>
                    <p>Album not found. <a href='/albums'>Return to list of albums.</a></p>
                    </body>
                    </html>
                    """;
        }
    }

    @GetMapping("/add")
    public String renderAddAlbumForm() {
        return """
                <html>
                <body>
                <form action='/albums/add' method='POST'>
                <p>Enter the details of an album</p>
                <input type='text' name='name' placeholder='Name' />
                <input type='text' name='artist' placeholder='Artist' />
                <input type='number' name='year' placeholder='Year' />
                <input type='number' name='tracks' placeholder='Tracks' />
                <button type='submit'>Add</button>
                </form>
                </body>
                </html>
                """;
    }

    @PostMapping("/add")
    public String processAddAlbumForm(@RequestParam(value="name") String name, @RequestParam(value="artist") String artist, @RequestParam(value="year") int year, @RequestParam(value="tracks") int tracks) {
        Album newAlbum = new Album(name, artist, year, tracks);
        albumRepository.save(newAlbum);
        return """
                <html>
                <body>
                <h3>ALBUM ADDED</h3>
                """ +
                "<p>You have successfully added " + name + " to the collection.</p>" +
                """
                <p><a href='/albums/add'>Add</a> another album or view the <a href='/albums'>updated list</a> of albums.</p>
                </body>
                </html>
                """;
    }

    @GetMapping("/update/{albumId}")
    public String updateAlbumDetails(@PathVariable(value = "albumId") int albumId) {
        Album currentAlbum = albumRepository.findById(albumId).orElse(null);
        if (currentAlbum != null) {
            return """
                    <html>
                    <body>
                    <h3>Update Artwork</h3>
                    """ +
                    "<form action='/albums/update/" + currentAlbum.getId() + "' method='POST'>" +
                    "<p>Update the details of a album:</p>" +
                    "<input type='text' name='name' value='" + currentAlbum.getName() + "' placeholder='Name' />" +
                    "<input type='text' name='artist' value='" + currentAlbum.getArtist() + "' placeholder='Artist' />" +
                    "<input type='number' name='year' value='" + currentAlbum.getYear() + "' placeholder='Year' />" +
                    "<input type='number' name='tracks' value='" + currentAlbum.getTracks() + "' placeholder='Tracks' />" +
                    "<button type='submit'>Update</button>" +
                    """
                    </body>
                    </html>
                    """;
        } else {
            return """
                    <html>
                    <body>
                    <h3>Update Album</h3>
                    <p>Album not found. <a href='/albums'>Return to list of albums.</a></p>
                    </body>
                    </html>
                    """;
        }
    }

    @PostMapping("/update/{albumId}")
    public String processUpdateAlbumDetails(@PathVariable(value = "albumId") int albumId, @RequestParam(value = "name") String name, @RequestParam(value = "artist") String artist, @RequestParam(value = "year") int year, @RequestParam(value = "tracks") int tracks) {
        Album albumToUpdate = albumRepository.findById(albumId).orElse(null);

        if (albumToUpdate != null) {
            albumToUpdate.setName(name);
            albumToUpdate.setArtist(artist);
            albumToUpdate.setYear(year);
            albumToUpdate.setTracks(tracks);

            albumRepository.save(albumToUpdate);

            return """
                <html>
                <body>
                <h3>ALBUM UPDATED</h3>
                """ +
                    "<p>You have successfully updated " + name + " to the collection.</p>" +
                    """
                    <p>View the <a href='/albums'>updated list</a> of albums.</p>
                    </body>
                    </html>
                    """;
        } else {
            return """
                    <html>
                    <body>
                    <h3>Update Album</h3>
                    <p>Album not found. <a href='/albums'>Return to list of albums.</a></p>
                    </body>
                    </html>
                    """;
        }
    }

    @GetMapping("/delete")
    public String renderDeleteAlbumForm() {
        List<Album> allAlbums = albumRepository.findAll();
        StringBuilder albumsList = new StringBuilder();
        for (Album album : allAlbums) {
            int currId = album.getId();
            albumsList.append("<li><input id='").append(currId).append("' name='albumIds' type='checkbox' value='").append(currId).append("' />").append(album.getName()).append(" - ").append(album.getArtist()).append("</li>");
        }
        return """
                <html>
                <body>
                <form action='/albums/delete' method='POST'>
                <p>Select which albums you wish to delete:</p>
                <ul>
                """ +
                albumsList +
                """
                </ul>
                <button type='submit'>Submit</button>
                </form>
                </body>
                </html>
                """;
    }

    @PostMapping("/delete")
    public String ProcessDeleteAlbumForm(@RequestParam(value="albumIds") int[] albumIds) {
        for (int id : albumIds) {
            Album currAlbum = albumRepository.findById(id).orElse(null);
            if (currAlbum != null) {
                albumRepository.deleteById(id);

            }
        }
        String header = albumIds.length > 1 ? "ALBUMS" : "ALBUM";
        return """
                <html>
                <body>
                <h3>
                """ +
                header +
                """
                DELETED</h3>
                <p>Deletion successful.</p>
                <p>View the <a href='/albums'>updated list</a> of albums.</p>
                </body>
                </html>
                """;
    }
}
