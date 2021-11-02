package connectfour

class Player(val name: String, val piece: String, var numberOfWins: Int = 0)

fun configureGame(): Pair<Pair<Player, Player>, Pair<Int, Int>>{
    val inputTemplate = Regex("\\s*[0-9]+\\s*[xX]\\s*[0-9]+\\s*")

    println("Connect Four")
    println("First player's name:")
    val firstPlayerName = readLine()!!
    val firstPlayer = Player(firstPlayerName, "o")
    println("Second player's name:")
    val secondPlayerName = readLine()!!
    val secondPlayer = Player(secondPlayerName, "*")
    var rows: Int
    var columns: Int
    while (true) {
        println("Set the board dimensions (Rows x Columns)\n" +
                "Press Enter for default (6 x 7)")
        var inp = readLine()!!
        if (inp == "") {
            rows = 6
            columns = 7
            break
        } else if (inp.matches(inputTemplate)) {
            inp = inp.replace(Regex("\\s+"), "") // spaces, tabs, etc to nothing
            val inpList = inp.split(Regex("[xX]"))
            rows = inpList[0].toInt()
            columns = inpList[1].toInt()
            if (rows !in 5..9) {
                println("Board rows should be from 5 to 9")
                continue
            }
            if (columns !in 5..9) {
                println("Board columns should be from 5 to 9")
                continue
            }
            break
        } else {
            println("Invalid input")
            continue
        }
    }
    val players = Pair(firstPlayer, secondPlayer)
    val boardSize = Pair(rows, columns)

    return Pair(players, boardSize)
}

fun makeBoard(rows: Int, columns: Int): MutableList<MutableList<String>> {
    return MutableList(rows) { MutableList<String>(columns) { " " } }
}

fun drawGameBoard(board: MutableList<MutableList<String>>, rows: Int, columns: Int){
    for (i in 1..columns) {
        print(" $i")
    }
    println()

    for (row in board.indices) {
        for (column in board[row].indices) {
            print("║")
            print(board[row][column])
        }
        println("║")
    }

    print("╚")
    for (i in 1 until columns) {
        print("═╩")
    }
    println("═╝")
}

enum class Turn {
    FIRST_PLAYER,
    SECOND_PLAYER
}

fun isVacant(board: MutableList<MutableList<String>>, placeToDrop: Int): Boolean {
    // column is vacant if the top of it is vacant!
    return board[0][placeToDrop] == " "
}

enum class Condition {
    FIRST_PLAYER_WON,
    SECOND_PLAYER_WON,
    DRAW,
    GAME_NOT_ENDED,
    UNEXPECTED_GAME_END
}

fun convertToString(board: MutableList<MutableList<String>>, columns: Int): String {
    var res = ""
    for(row in board.indices) {
        var tmp = board[row].toString().replace(Regex("(, )"), "")
                .replace(Regex("[\\[\\]]"), "")
        res += tmp
    }
    return res
}

fun checkCondition(board: MutableList<MutableList<String>>, rows: Int, columns: Int): Condition {
    val positions = convertToString(board, columns)
//    println(positions)

    var allFull: Boolean = true
    for(column in 0 until columns) {
        if(board[0][column] == " ") {
            allFull = false
        }
    }
    if(allFull) {
        return Condition.DRAW
    }

    // Horizontally
    for(row in 0 until rows) {
        var line: String = ""
        for (column in 0 until columns) {
            line += board[row][column]
        }
        if(line.contains(Regex("o{4}"))) {
            return Condition.FIRST_PLAYER_WON
        } else if(line.contains(Regex("\\*{4}"))) {
            return Condition.SECOND_PLAYER_WON
        }
    }

    // Vertically
    for(column in 0 until columns) {
        var line: String = ""
        for (row in 0 until rows) {
            line += board[row][column]
        }
        if(line.contains(Regex("o{4}"))) {
            return Condition.FIRST_PLAYER_WON
        } else if(line.contains(Regex("\\*{4}"))) {
            return Condition.SECOND_PLAYER_WON
        }
    }

    // Diagonally1
    for(delta in 0..columns - 4) {
        var line1: String = ""
        var row = delta
        var column = 0
        while(row < rows && column < columns) {
            line1 += board[row][column]
            row += 1
            column += 1
        }
//        println("line1: $line1")
        if(line1.contains(Regex("o{4}"))) {
            return Condition.FIRST_PLAYER_WON
        } else if(line1.contains(Regex("\\*{4}"))) {
            return Condition.SECOND_PLAYER_WON
        }
        var line2: String = ""
        row = 0
        column = delta
        while(row < rows && column < columns) {
            line2 += board[row][column]
            row += 1
            column += 1
        }
//        println("line2: $line2")
        if(line2.contains(Regex("o{4}"))) {
            return Condition.FIRST_PLAYER_WON
        } else if(line2.contains(Regex("\\*{4}"))) {
            return Condition.SECOND_PLAYER_WON
        }
    }

    // Diagonally2
    for(delta in 0..rows - 4) {
        var line1: String = ""
        var row = 0
        var column = columns - 1 - delta
        while(row < rows && column >= 0) {
            line1 += board[row][column]
            row += 1
            column -= 1
        }
//        println("line1: $line1")
        if(line1.contains(Regex("o{4}"))) {
            return Condition.FIRST_PLAYER_WON
        } else if(line1.contains(Regex("\\*{4}"))) {
            return Condition.SECOND_PLAYER_WON
        }
        var line2: String = ""
        row = delta
        column = columns - 1
        while(row < rows && column >= 0) {
            line2 += board[row][column]
            row += 1
            column -= 1
        }
//        println("line2: $line2")
        if(line2.contains(Regex("o{4}"))) {
            return Condition.FIRST_PLAYER_WON
        } else if(line2.contains(Regex("\\*{4}"))) {
            return Condition.SECOND_PLAYER_WON
        }
    }

    return Condition.GAME_NOT_ENDED
}

fun gameplay(board: MutableList<MutableList<String>>, rows: Int, columns: Int,
                firstPlayer: Player, secondPlayer: Player, game: Int): Condition {
    var turn: Turn
    if(game % 2 == 1) {
        turn = Turn.FIRST_PLAYER
    } else {
        turn = Turn.SECOND_PLAYER
    }
    var piece: String
    var condition = Condition.GAME_NOT_ENDED
    drawGameBoard(board, rows, columns)
    while(true) {
        if (turn == Turn.FIRST_PLAYER) {
            println("${firstPlayer.name}'s turn:")
        } else {
            println("${secondPlayer.name}'s turn:")
        }
        var action = readLine()!!
        if (action.matches(Regex("\\s*end\\s*")) || condition != Condition.GAME_NOT_ENDED) {
            return Condition.UNEXPECTED_GAME_END
        } else if (action.matches(Regex("[0-9]+"))) {
            val inp = action.toInt()
            val placeToDrop = inp - 1
            if(placeToDrop in 0 until columns) {
                if(isVacant(board, placeToDrop)) {
                    if (turn == Turn.FIRST_PLAYER) {
                        piece = firstPlayer.piece
                        turn = Turn.SECOND_PLAYER
                    } else {
                        piece = secondPlayer.piece
                        turn = Turn.FIRST_PLAYER
                    }
                    var bottom = true
                    for (i in 0 until rows) {
                        if (board[i][placeToDrop] != " ") {
                            board[i - 1][placeToDrop] = piece
                            bottom = false
                            break
                        }
                    }
                    if (bottom) {
                        board[rows - 1][placeToDrop] = piece
                    }

                    drawGameBoard(board, rows, columns)

                    condition = checkCondition(board, rows, columns)
                    when(condition) {
                        Condition.FIRST_PLAYER_WON -> {
                            println("Player ${firstPlayer.name} won")
                            firstPlayer.numberOfWins += 2
                            return Condition.FIRST_PLAYER_WON
                        }
                        Condition.SECOND_PLAYER_WON -> {
                            println("Player ${secondPlayer.name} won")
                            secondPlayer.numberOfWins += 2
                            return Condition.SECOND_PLAYER_WON
                        }
                        Condition.DRAW -> {
                            println("It is a draw")
                            firstPlayer.numberOfWins += 1
                            secondPlayer.numberOfWins += 1
                            return Condition.DRAW
                        }
                    }
                    continue
                } else {
                    println("Column $inp is full")
                    continue
                }
            } else {
                println("The column number is out of range (1 - $columns)")
                continue
            }
        } else {
            println("Incorrect column number")
        }
    }
}



fun main() {
    val gameInfo = configureGame()
    val firstPlayer = gameInfo.first.first
    val secondPlayer = gameInfo.first.second
    val rows = gameInfo.second.first
    val columns = gameInfo.second.second

    var numberOfGames = -1
    while (numberOfGames == -1) {
        println("Do you want to play single or multiple games?\n" +
                "For a single game, input 1 or press Enter\n" +
                "Input a number of games:")
        val inp = readLine()!!
        if(inp.matches(Regex("[1-9][0-9]*"))) {
            numberOfGames = inp.toInt()
        } else if (inp == "") {
            numberOfGames = 1
        } else {
            println("Invalid input")
        }
    }

    println("${firstPlayer.name} VS ${secondPlayer.name}")
    println("$rows X $columns board")

    if(numberOfGames == 1) {
        println("Single game")
    } else {
        println("Total $numberOfGames games")
    }

    // Gameplay
    if (numberOfGames == 1) {
        var gameBoard = makeBoard(rows, columns)
        gameplay(gameBoard, rows, columns, firstPlayer, secondPlayer, 1)
    } else {
        for (game in 1..numberOfGames) {
            var gameBoard = makeBoard(rows, columns)
            println("Game #$game")
            val result = gameplay(gameBoard, rows, columns, firstPlayer, secondPlayer, game)
            if(result != Condition.UNEXPECTED_GAME_END) {
                println("Score\n" +
                        "${firstPlayer.name}: ${firstPlayer.numberOfWins} " +
                        "${secondPlayer.name}: ${secondPlayer.numberOfWins}")
            } else {
                break
            }
        }
    }
    println("Game over!")
}