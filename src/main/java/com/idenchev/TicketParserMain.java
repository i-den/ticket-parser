package com.idenchev;

public class TicketParserMain {
    public static void main(String[] args) {
         TicketParser ticketParser = new ConsoleTicketParser();
         ticketParser.parse();
    }
}
