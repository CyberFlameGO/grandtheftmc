package net.grandtheftmc.core.boards;

import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.core.users.User;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Board {

    private String name;

    private String header;

    private BoardType type;

    private List<BoardValue> boardValues = new ArrayList<>();

    private List<String> list = new ArrayList<>();

    private List<BoardScore> scores = new ArrayList<>();

    public Board(String name, String header, List<BoardValue> values) {
        this.name = name;
        this.header = header;
        this.boardValues = values;
        this.type = BoardType.KEY_VALUE;
    }

    public Board(String name, String header, String[] lines) {
        this.name = name;
        this.header = header;
        this.list = Arrays.asList(lines);
        this.type = BoardType.LIST;
    }

    public Board(String name, List<BoardScore> scores, String header) {
        this.name = name;
        this.header = header;
        this.scores = scores;
        this.type = BoardType.SCORES;
    }

    public Board(String name, String header, BoardType type) {
        this.name = name;
        this.header = header;
        this.type = type;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHeader() {
        return this.header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public BoardType getType() {
        return this.type;
    }

    public void setType(BoardType type) {
        this.type = type;
    }

    public List<BoardValue> getBoardValues() {
        return this.boardValues;
    }

    public void setBoardValues(List<BoardValue> boardValues) {
        this.boardValues = boardValues;
    }

    public List<String> getList() {
        return this.list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    public List<BoardScore> getScores() {
        return this.scores;
    }

    public void setScores(List<BoardScore> scores) {
        this.scores = scores;
    }

    public Board addValue(String color, String name, String value) {
        this.boardValues.add(new BoardValue(color, name, value));
        return this;
    }

    public Board addScore(String name, int score) {
        this.scores.add(new BoardScore(name, score));
        return this;
    }

    public Board addLine(String line) {
        this.list.add(line);
        return this;
    }

    public void updateFor(Player player, User user) {
        List<BoardScore> scores = new ArrayList<>();
        switch (this.type) {
        case KEY_VALUE: {
            int currentLine = 15;
            int n = 5;
            for (BoardValue value : this.boardValues) {
                scores.add(new BoardScore("&" + n--, currentLine--));
                scores.add(new BoardScore('&' + value.getColor() + "&l" + value.getName(), currentLine--));
                scores.add(new BoardScore(' ' + value.getValue(), currentLine--));
            }
        }
        break;
        case LIST:
            int currentLine = 15;
            for (String line : this.list) {
                scores.add(new BoardScore(line, currentLine));
                currentLine--;
            }
        break;
        case SCORES:
            scores = this.scores;
        break;
        }
        this.updateFor(player, user, scores);
    }

    private void updateFor(Player player, User user, List<BoardScore> scores) {
        Scoreboard scoreboard = user.getScoreboard();
        Objective obj = scoreboard.getObjective(player.getName());
        if (obj == null)
            obj = scoreboard.registerNewObjective(player.getName(), "dummy");
        for (String s : scoreboard.getEntries())
            if (obj.getScore(s).isScoreSet())
                scoreboard.resetScores(s);
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        obj.setDisplayName(Utils.f(this.header));
        List<String> playerNames = new ArrayList<>();
        for (BoardScore boardScore : scores) {
            int lineNumber = boardScore.getScore();

            String originalLine = boardScore.getName();
            String originalTeamPrefix = "";
            String originalPlayerName;

            if (originalLine.length() > 32) {
                originalTeamPrefix = originalLine.substring(0, 16);
                originalPlayerName = originalLine.substring(16, 32);
            } else if (originalLine.length() > 16) {
                originalTeamPrefix = originalLine.substring(0, originalLine.length() - 16);
                originalPlayerName = originalLine.substring(originalLine.length() - 16, originalLine.length());
            } else {
                originalPlayerName = originalLine;
            }
            String line;
            String teamPrefix = originalTeamPrefix;
            String playerName = originalPlayerName;
            int t = 0;
            while (playerNames.contains(playerName)) {
                line = originalTeamPrefix + originalPlayerName;
                if (line.length() > 30)
                    line = line.substring(0, 30);
                line = line + '&' + t;
                t++;
                if (line.length() > 32) {
                    teamPrefix = line.substring(0, 16);
                    playerName = line.substring(16, 32);
                } else if (line.length() > 16) {
                    teamPrefix = line.substring(0, line.length() - 16);
                    playerName = line.substring(line.length() - 16, line.length());
                } else {
                    playerName = line;
                }
            }

            playerNames.add(playerName);
            Score score = obj.getScore(Utils.f(playerName));
            if (!teamPrefix.isEmpty()) {
                Team team = scoreboard.getTeam(playerName);
                if (team == null)
                    team = scoreboard.registerNewTeam(playerName);
                team.setPrefix(Utils.f(teamPrefix));
                team.setSuffix("");
                team.addEntry(Utils.f(playerName));
//                team.setNameTagVisibility(NameTagVisibility.NEVER);
            }
            score.setScore(lineNumber);
        }

        player.setScoreboard(scoreboard);
    }

}
