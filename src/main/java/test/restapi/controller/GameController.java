package test.restapi.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import test.restapi.model.Game;
import test.restapi.model.Move;
import test.restapi.model.Player;
import test.restapi.model.State;

import java.util.Map;
import java.util.UUID;

/**
 * The REST controller class for the RockPaperScissorsApplication
 */

@RestController
public class GameController {

    public Game game;
    public Player player1;
    public Player player2;

    /**
     * Controller method for creating a new game
     * @param body request body
     * @return player name and game id
     */
    @RequestMapping(method = RequestMethod.POST, value = "/api/games", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> newGame(@RequestBody Map<String, String> body) {
        player1 = new Player(body.get("name"));
        game = new Game(State.STARTED);
        game.setPlayer1(player1);

        return ResponseEntity.status(HttpStatus.CREATED).body("Player 1 joined: " + player1.getName() + " " + "Game ID is: " + game.getGameID() + "\n");
    }

    /**
     * Controller method for a new player joining the game
     * @param id game id
     * @param body request body
     * @return player name
     */
    @RequestMapping(method = RequestMethod.POST, value = "/api/games/{id}/join", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> joinGame(@PathVariable UUID id, @RequestBody Map<String, String> body) {
        player2 = new Player(body.get("name"));

        if(!id.equals(game.getGameID())) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Game ID was not found"+ "\n");

        }
        game.setPlayer2(player2);
        return ResponseEntity.status(HttpStatus.OK).body("Player 2 joined: " + player2.getName()+ "\n");
    }

    /**
     * Controller method for a player making a move
     * @param id game id
     * @param body request body
     * @return a player's name and move
     */
    @RequestMapping(method = RequestMethod.POST, value = "/api//games/{id}/move", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> makeMove(@PathVariable UUID id, @RequestBody Map<String, String> body) {

        if(!id.equals(game.getGameID())) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Game ID was not found"+ "\n");
        }

        if (body.get("name").equals(player1.getName())) {
            player1.setMove(Move.valueOf(body.get("move")));

            return ResponseEntity.status(HttpStatus.OK).body("Player 1: " + player1.getName() + " made a move: " + player1.getMove().toString()+ "\n");

        }
        else if (body.get("name").equals(player2.getName())) {
            player2.setMove(Move.valueOf(body.get("move")));

            return ResponseEntity.status(HttpStatus.OK).body("Player 2: " + player2.getName() + " made a move " + player2.getMove().toString()+ "\n");

        }
        else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Player is not part of this game"+ "\n");
        }
    }

    /**
     * Controller method for the current state of the game
     * @param id game ID
     * @return the current game status
     */
    @RequestMapping(method = RequestMethod.GET, value = "/api/games/{id}")
    public ResponseEntity<String> checkState(@PathVariable UUID id) {

        if(!id.equals(game.getGameID())) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Game ID was not found"+ "\n");

        }
        game.evaluateMoves(player1, player2);

        return ResponseEntity.status(HttpStatus.OK).body(game.toString());
    }
}
