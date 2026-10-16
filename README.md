# STAGE 1 WORK IN PROGRESS

# TO BE IMPLEMENTED (HIGH PRIORITY)
1. DisplayController : Controls which display to use: Start Display, End Display, etc.
2. ManagerMenu : Opens up a numbered menu for User to choose action (Easier to Implement)
3. GameController : The core to run all classes and objects
4. GameApp : Start entry of the program and initialisation of all core classes
5. Cashier : Payment Calculation Method
6. Exception Handling for ALL classes
7. Adjust Access Modifiers accordingly to security needs (Public/Protected/Private) for ALL classes

## MISSING PROJECT REQUIREMENT (HIGH PRIORITY)
1. For Manager Choice, invalid options chosen like asking Waiter to seat Customer when theres no one in the Queue! <-- Won't consume action point and just tell Manager to choose again and explain why previously chosen option is invalid
2. Staff Tasks Available for Start-ofturn display (Not sure if using the describeToDos method under Cashier/Chef/Waiter OR thru some logic to tell which is available/ not available)
3. GameController, Ensure that Waiter RUNs first then Chef. This is to ensure Chef can cook the current turn order taken by Waiter BUT NOT Waiter served READY food made in the SAME turn

## Future Design Considerations
1. Order Class functions as a Receipt too so Cashier can update the price set in Order and charge accordingly (ONGOING)
2. Since BaseSetting allows the TA to edit no. of Cashier/Chef/Waiter, no. of actions. Must be ready to adjust the game accordingly (LOW PRIORITY)


# Game Design IMPLEMENTED

## View Restrictions
- StateView only allows retrieval Customer and Order lists sorted by Status from StateManager Class
- TableView only allows retrieval of table array from Restaurant Class

## Restaurant Class Split into StateManager & Restaurant
- Restaurant Class now ONLY handles restaurant operations (Processes only)
- Restuarant owns table so includes an array to store table
- StateManager stores both Customer n Order States and performs operations relating to status
- StateManager uses MAP {KEY:VALUE}. Each status is a key and its value are list of Customer/Order

## OTHERS
- BaseSetting stores default configurations, subject to TA changes
- No MENU class, list to store 3 MenuItem instead since they are considered BASE CONFIGURATION


# REFLECTION :(
- Underestimated the workload, Should have done UML diagram first to track work required


----------------------------------------------------------------------

# NTUBurger

A Java restaurant game for SC2002 (Object-Oriented Design & Programming). Players manage customers, waiters, chefs and a cashier across three stages.

## Features
- Menu and order management
- Roles: customer, waiter, chef, cashier
- Three game stages

## Requirements (TO BE CONFIRMED)
- Java JDK 17 or later (adjust to your version)

## How to Run (TO BE CONFIRMED)
1. Clone the repo
2. Open the folder in VS Code
3. Run `GameApp.java`

## Project Structure
- `Restaurant/`: menu and restaurant classes
- `Roles/`: role classes
- `GameApp.java`: entry point

## Team
- Your Name
- Teammate names