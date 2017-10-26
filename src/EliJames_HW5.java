/*************************************************************************
 * Title: Loan Eligibility Calculator
 * File: EliJames_HW5.java
 * Author: James Eli
 * Date: 2/1/2017
 *
 * This JavaFX program calculates loan eligibility based upon annual
 * income vs. loan payment. Eligibility is determined by monthly income 
 * being within a percentage of the monthly loan payment. The threshold 
 * percentage for eligibility is defined by the value:
 * MAXIMUM_INCOME_TO_LOAN_PERCENTAGE, this value can be changed if needed.
 *
 * Notes: 
 *   (1) Compiled with java SE JDK 8, Update 121 (JDK 8u121) and JavaFX
 *   version 8.0.121-b13.
 *   
 * Submitted in partial fulfillment of the requirements of PCC CIS-279.
 *************************************************************************
 * Change Log:
 *   02/01/2017: Initial release. JME
 *************************************************************************/
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.geometry.HPos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.beans.*;

public class EliJames_HW5 extends Application {
  // Maximum monthly income-to-loan payment percentage. Exceeding this percentage means not eligible for loan.  
  public static final double MAXIMUM_INCOME_TO_LOAN_PERCENTAGE = 25.0;

  // Textfield array indexes.
  private static final int TOTAL_INCOME = 0;
  private static final int MONTHLY_PAYMENT = 1;
  private static final int TOTAL_PAYMENT = 2;
  private static final int ANNUAL_INTEREST_RATE = 4;
  private static final int TERM_IN_YEARS = 5;
  private static final int LOAN_AMOUNT = 6;

  // Minimum and maximum input values.
  public static final double MINIMUM_INCOME_INPUT = 0.0d;
  public static final double MAXIMUM_INCOME_INPUT = 1000000.0d;
  public static final double MINIMUM_INTEREST_RATE = 0.0d;
  public static final double MAXIMUM_INTEREST_RATE = 25.0d;
  public static final double MINIMUM_TERM_IN_YEARS = 1.0d;
  public static final double MAXIMUM_TERM_IN_YEARS = 30.0d;
  public static final double MINIMUM_LOAN_AMOUNT = 0.0d;
  public static final double MAXIMUM_LOAN_AMOUNT = 1000000.0d;
  
  // Tooltips and dialog text.
  private final String[] ttText = new String[] {
    "Annual salary and wages must\nbe between 0 and $1M\n",
    "Annual interest income must\nbe between 0 and $1M\n",
    "Annual investment income must be\nbetween 0 and $1M\n",
    "Annual other income must be\nbetween 0 and $1M\n",
    "Annual interest must be\n between 0 and 25.0%\n",
    "Term in years must be between\n1 to 30 (whole, no decimals) years\n",
    "Loan amount must be\nbetween 0 and $1M\n"
  };

  // 7 textfields used as inputs.
  private final TextField[] tfInputs = new TextField[7];
  // 3 textfields used as outputs.
  private final TextField[] tfOutputs = new TextField[3];

  // Loan eligibility text.
  private final Text txtEligibility = new Text( "" );
  
  // Flags output states, set to true when output has been displayed.
  private static boolean incomeOutput = false;
  private static boolean loanOutput = false;

  /**********************************
   * JavaFX application start method. 
   *********************************/
  @Override
  public void start( Stage primaryStage ) {
    // Create UI.
    GridPane gridPane = new GridPane();
    gridPane.setAlignment( Pos.CENTER );
    gridPane.setHgap( 5 );
    gridPane.setVgap( 5 );

    // Setup textfields and labels.
    initTextFields( gridPane );
	    
    // Set area for loan eligibility text, spanning gridpane column 2 and 3 to fit.
    gridPane.add( txtEligibility, 2, 5, 3, 5 );

    // Set 2 buttons with mnemonics.
    Button btCalculate = new Button( "Calc _Payment" );
    Button btCancel = new Button( "_Cancel" );
    gridPane.add( btCalculate, 0, 5 );
    gridPane.add( btCancel, 1, 5 );
    // Position buttons.
    GridPane.setHalignment( btCalculate, HPos.LEFT );
    GridPane.setHalignment( btCancel, HPos.LEFT );
    // Instantiate event handler and pass the form to it using "this".
    CalculateButtonHandler calcButtonHandler = new CalculateButtonHandler( this );
    // Associate button btCalculate ("Calculate Payment") with EventHandler calcButtonHandler.
    btCalculate.setOnAction( calcButtonHandler ); 
    // Register cancel button event handler.
    btCancel.setOnAction( e -> Platform.exit() );

    // Create the scene and place it in the stage
    Scene scene = new Scene( gridPane, 650, 200 );
    // Set window title.
    primaryStage.setTitle( "Loan Payment and Eligibility Calculator Form" );
    primaryStage.setScene( scene ); // Place the scene in the stage.
    primaryStage.show();            // Display the stage.
  }
  
  /*****************************************************************
   * Setup textfields and labels.
   ****************************************************************/
  private void initTextFields( GridPane gridPane ) {
    // Input and output label texts.
    final String[] tfInLabels = new String[] { 
      "Salary and Wages:", "Interest Income:", "Investment Income:", "Other Income:",
      "Annual Interest Rate (n.nn%):", "Term in Years:","Loan Amount:" 
    };
    final String[] tfOutLabels = new String[] { 
      "Total Income:", "Monthly Payment:", "Total Payments over Life of Loan:" 
    };
	 
    // Tooltips.
    final Tooltip[] tooltip = new Tooltip[7];

    // Grid pane column and row positions for textfields and labels.
    final int[] inColumns = new int[] { 1, 1, 1, 1, 3, 3, 3, 0, 0, 0, 0, 2, 2, 2 };
    final int[] inRows = new int[] { 0, 1, 2, 3, 0, 1, 2 };
    final int[] outColumns = new int[] { 0, 2, 2 };
    final int[] outRows = new int[] { 4, 3, 4 };

    // Setup output textfields.
    for ( int i=0; i<3; i++ ) {
      tfOutputs[i] = new TextField();
      gridPane.add( new Label( tfOutLabels[i] ), outColumns[i], outRows[i] );
      gridPane.add( tfOutputs[i], outColumns[i] + 1, outRows[i] );
      tfOutputs[i].setAlignment( Pos.BOTTOM_RIGHT );
      tfOutputs[i].setEditable( false );         // Not editable.
      tfOutputs[i].setFocusTraversable( false ); // Cannot receive focus.
    }
    
    // Setup input textfields.
    for ( int i=0; i<7; i++ ) {
      tfInputs[i] = new TextField();
      gridPane.add( tfInputs[i], inColumns[i], inRows[i] );
      gridPane.add( new Label( tfInLabels[i] ), inColumns[i+7], inRows[i] );
      tfInputs[i].setAlignment( Pos.BOTTOM_RIGHT );

      // IvalidationListeners clear output textfields when new data is entered, eliminating incorrect/stale data.  
      if ( i < ANNUAL_INTEREST_RATE ) { 
        // Income textfields.
 	    tfInputs[i].textProperty().addListener( new InvalidationListener() {
      	  public void invalidated( Observable o ) {
    	    // Check output state.
    	    if ( incomeOutput ) {
    	      tfOutputs[TOTAL_INCOME].setText( "" );
    	      incomeOutput = false; // Reset state.
    	      txtEligibility.setText( "" );
    	    }
      	  }
        } );
      } else {
        // Loan data textfields.
  	    tfInputs[i].textProperty().addListener( new InvalidationListener() {
       	  public void invalidated( Observable o ) {
    	    // Check output state.
    	    if ( loanOutput ) { 
    	      tfOutputs[TOTAL_PAYMENT].setText( "" );
    	      tfOutputs[MONTHLY_PAYMENT].setText( "" );
    	      txtEligibility.setText( "" );
    	      loanOutput = false; // Reset state.
    	    }
       	  }
    	} );
      }
      
      // Add tooltips for input textfields.
      tooltip[i] = new Tooltip();
      tooltip[i].setText( ttText[i] );
      tfInputs[i].setTooltip( tooltip[i] );
      
      // Set id for textfields. Used to identify the textfield when an exception is thrown.
      tfInputs[i].setId( Integer.toString(i) );
    }
  }
  
  /*****************************************************************
   * Attempt to convert textfield input from String to double value.
   ****************************************************************/
  private double getDoubleFromTextField( TextField tf ) throws IllegalArgumentException {
    if ( tf.getText() != null && tf.getLength() > 0 ) { 
      // Parse for valid money, percentage and maximum of 2 decimal places.
      if ( tf.getText().matches( "[\\$]?([\\$]?\\d{1,3}(\\,\\d{3})*|\\d*)(\\.)?(\\d{0,2})?[\\%]?" ) ) { 
        return Double.parseDouble( tf.getText().toString().trim()
                                     .replaceAll( "\\$", "" )
                                     .replaceAll( "\\%", "" )
                                     .replaceAll( ",", "" ) );
      } else
        // Throw iae and pass index of textfield.
        throw new IllegalArgumentException( tf.getId() );
    }
    
    // An empty textfield is returned as 0.
    return 0.0d;
  }
  
  /*****************************************
   * Event handler for the calculate button.
   ****************************************/
  private void calculateLoanPayment() {
    double totalIncome = 0.0d;  // Total of all forms of income.
    double loanInterest = 0.0d; // Loan annual interest rate.
    double loanAmount = 0.0d;   // Loan amount.
    int loanTerm = 0;           // Loan term in years.
    double d;                   // Temporary holder for input values.
    
    try {
   	  // Attempt to retrieve inputs and validate. Failed validation throws an iae 
      // and our catch block (below) then displays an error dialog.
      for ( int i=0; i<4; i++ ) { // Loop through all income inputs.
        d = getDoubleFromTextField( tfInputs[i] );
        if ( d < MINIMUM_INCOME_INPUT || d > MAXIMUM_INCOME_INPUT )
          throw new IllegalArgumentException( String.valueOf(i) );
        totalIncome += d;
   	  }
      tfOutputs[TOTAL_INCOME].setText( String.format( "$%.2f", totalIncome ) );
      incomeOutput = true; // Set output state.

      // Validate annual interest rate input.
      d = getDoubleFromTextField( tfInputs[ANNUAL_INTEREST_RATE] );
      if ( d < MINIMUM_INTEREST_RATE || d > MAXIMUM_INTEREST_RATE )
        throw new IllegalArgumentException( String.valueOf(ANNUAL_INTEREST_RATE) );
      loanInterest = d;
      
      // Validate term in years input.
      d = getDoubleFromTextField( tfInputs[TERM_IN_YEARS] );
      if ( d < MINIMUM_TERM_IN_YEARS || d > MAXIMUM_TERM_IN_YEARS )
        throw new IllegalArgumentException( String.valueOf(TERM_IN_YEARS) );
      // Ensure textfield contains an integer value (no decimal values allowed).
      if ( d != (int)d ) 
        throw new IllegalArgumentException( String.valueOf(TERM_IN_YEARS) );
      loanTerm = (int)d;

      // Validate loan amount input.
      d = getDoubleFromTextField( tfInputs[LOAN_AMOUNT] );
      if ( d < MINIMUM_LOAN_AMOUNT || d > MAXIMUM_LOAN_AMOUNT )
        throw new IllegalArgumentException( String.valueOf(LOAN_AMOUNT) );
      loanAmount = d;

      // Create a loan object. Loan defined in Textbook Listing 10.2
      Loan loan = new Loan( loanInterest, loanTerm, loanAmount );
      // Display monthly payment and total payment
      tfOutputs[MONTHLY_PAYMENT].setText( String.format( "$%.2f", loan.getMonthlyPayment() ) );
      tfOutputs[TOTAL_PAYMENT].setText( String.format( "$%.2f", loan.getTotalPayment() ) );
      loanOutput = true; // Set output state.

      // Check if loan payment is within income-to-loan percentage limit.
  	  double percentage = loan.getMonthlyPayment()/(totalIncome/1200.);
      if ( percentage <= MAXIMUM_INCOME_TO_LOAN_PERCENTAGE ) { 
        txtEligibility.setFill( Color.GREEN );
        txtEligibility.setText( String.format( "Eligible for loan! Loan to income percentage: %2.1f%%", percentage ) );
      } else {
        txtEligibility.setFill( Color.RED );
        txtEligibility.setText( String.format( "Not Eligible for loan! Loan to income percentage: %2.1f%%", percentage ) );
      }

    } catch ( Exception ex ) {
      // Throw up an error dialog.
      Alert alert = new Alert( AlertType.ERROR );
      alert.setTitle( "Input Error" );
      alert.setHeaderText( null );
      // Catch the iae exception we've thrown?
      if ( ex.toString() != null && ex.toString().contains( "IllegalArgumentException" ) ) {
        int i = Integer.parseInt( ex.getMessage() );
        alert.setContentText( ttText[i] );
        tfInputs[i].requestFocus();
      } else 
        alert.setContentText( "There is a problem with your input!" );
      alert.showAndWait();
    }
  }
  
  /**************************************************
   * EventHandler class for Calculate Payment button. 
   *************************************************/
  class CalculateButtonHandler implements EventHandler<ActionEvent> {
    EliJames_HW5 formObj = null; // Object of the form class.

    // Constructor receives an object of the form class.
    public CalculateButtonHandler( EliJames_HW5 formObj ) { 
      this.formObj = formObj;
    }

    // Call calculateLoanPayment method in LoanCalculator class.
    public void handle( ActionEvent e ) {
      formObj.calculateLoanPayment(); 
    }
  } // End CalculateButtonHandler class.

  // The main method is only needed for the IDE with limited JavaFX support. 
  // Not needed for running from the command line.
  public static void main( String[] args ) { launch( args ); }

} // End EliJames_HW5 class.
