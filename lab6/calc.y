%{
#include <stdio.h>
#include <stdlib.h>

void yyerror(char *s);
int yylex(void);

void Expression();        
void ExpressionPrime();   
void Term();             
void TermPrime();        
void Factor();

extern int yytoken;

int result = 0;  // Add this to store the result
%}

%token PLUS MULT LPAREN RPAREN NUM

%%
start: Expression
     ;

Expression: /* Empty rule since we handle it in C code */
     ;

%%

void Expression() {
    printf("Processing expression...\n");  // Add debug output
    Term();
    ExpressionPrime();
}

void ExpressionPrime() {
    if (yytoken == PLUS) {
        printf("Found + operator\n");  // Add debug output
        yylex();  // consume the '+'
        Term();
        ExpressionPrime();
    }
    // else EPSILON production - do nothing
}

void Term() {
    Factor();
    TermPrime();
}

void TermPrime() {
    if (yytoken == MULT) {
        printf("Found * operator\n");  // Add debug output
        yylex();  // consume the '*'
        Factor();
        TermPrime();
    }
    // else EPSILON production - do nothing
}

void Factor() {
    if (yytoken == LPAREN) {
        printf("Found (\n");  // Add debug output
        yylex();  // consume '('
        Expression();
        if (yytoken == RPAREN) {
            printf("Found )\n");  // Add debug output
            yylex();  // consume ')'
        }
        else
            yyerror("Expected ')'");
    }
    else if (yytoken == NUM) {
        printf("Found number\n");  // Add debug output
        yylex();  // consume number
    }
    else {
        yyerror("Syntax error in factor");
    }
}

void yyerror(char *s) {
    fprintf(stderr, "Error: %s\n", s);
    exit(1);
}

int main() {
    printf("Enter an expression (end with newline and Ctrl+D):\n");  // Add prompt
    yylex();  // get first token
    Expression();      // start parsing
    if (yytoken != 0)  // 0 is EOF
        yyerror("Extra characters at end of input");
    printf("Parsing completed successfully!\n");  // Add completion message
    return 0;
} 