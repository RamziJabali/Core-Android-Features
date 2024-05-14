# MVVM vs MVI

## What are they?

* MVVM = Model View View Model , MVI = Model View Intent
*  They are both presentational patterns
    * Meant to separte your presentation layer into different parts

## What is the model?

* The implement the business rules and business logic
* They implement project wide requirements
    * Ex:
    * ```
      data class User(
        val id: Int,
        val name: String,
        val email: String
      )
      ```
* These data classes are models and are shared between both MVVM and MVI


## What is the view?

* The view would also be the same and it would be a composable or an XML view

## ViewModel vs Intent?

* MVVM has ViewModel as a part of it's abbreviation and MVI has Intent
   * Though on Android they will both most likey both be done with a viewmodel.
* The job of the viewmodel is to contain state mapping logic
   * It processes incoming UI actions like a button click or refresh swipe and then decides based on the action how the state looks like afterwards. Like showing loading indicator which really dumbs down the view.

* The model the view and the viewmode, in most cases, is the same between both patterns.

## So what is the difference?

### MVVM has multiple state fields, vs MVI which has one state field or a single source of truth.

### Example MVVM

```
class MvvmViewModel(
   private val savedStateHandle: SavedStateHandle): ViewModel() {
   var postDetails by mutableStateOf<Post?>(null)
      private set

   var isLoading by mutableStateOf(false)
      private set

   var isPostLiked by mutableStateOf(false)
      private set

   init {
      savedStateHandle.get<String>("postId")?.let { postId ->
         loadPost(postId)
      }
   }
}

@Composable
fun MvvmScreen(
   postDetails: Post?,
   isLoading: Boolean,
   isPostLiked: Boolean,
   onToggleLike: () -> Unit,
   onBackCLick: () -> Unit
){....}
```

### Example MVI

```
class MvvmViewModel(
   private val savedStateHandle: SavedStateHandle): ViewModel() {
   var state by mutableStateOf(MviState())
      private set

   init {
      savedStateHandle.get<String>("postId")?.let { postId ->
         loadPost(postId)
      }
   }

   fun onAction(action: Action){
      when(action) {
         MviAction.ToggleLike -> toggleLike()
         else -> Unit
      }
   }

   fun toggleLike(){
      state.postDetails?.let{
         state.update { prevState->
            prevState.copy(isliked = !prevState.isLiked)
         }
      }
   }
}

data class MviState(
   val postDetails: Post? = null,
   val isLoading: Boolean = false,
   val isPostLiked: Boolean = false,
   val email: String = "",
   val isEmailValid: Boolean
)

@Composable
fun MviScreen(
   state: MviState,
   onAction: (MviAction) -> Unit
){....}
```

### Summary

The diffrence is state encapsulation and the way the state is packaged and delivered to the view.

### What would I be choosing and why?

MVI - Because it allows for state encapsulation and much more legible code. It's also a lot more predictible due to the single source of truth.



