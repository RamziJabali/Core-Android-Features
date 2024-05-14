# MVVM vs MVI

## What are they

1.MVVM = Model View View Model , MVI = Model View Intent
2.  They are both presentational patterns
    * Meant to separte your presentation layer into different parts

## What is a model

1. The implement thoe business rules and business logic
2. They implement project wide requirements
    * Ex:
    * ```
      data class User(
        val id: Int,
        val name: String,
        val email: String
      )
      ```
