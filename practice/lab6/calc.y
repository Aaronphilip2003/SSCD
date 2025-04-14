%{
#include<stdio.h>
#include<stdlib.h>

void yyerror(char *s);
int yylex(void);

void Expression();
void ExpressionPrime();
void Term();
void TermPrime();
void Factor();

extern int yytoken;
int result = 0;
%}

%token PLUS MULT LPAREN RPAREN NUM ID

%%
start: Expression
;

Expression : 
;
%%


void Expression(){
    printf("Processing expression...\n");
    Term();
    ExpressionPrime();
}

void ExpressionPrime(){
    if(yytoken == PLUS) {
        printf("Found + Operator\n");
        yylex();
        Factor();
        ExpressionPrime();
    }
}

void Term() {
    printf("Processing term...\n");
    Factor();
    TermPrime();
}

void TermPrime() {
    if(yytoken == MULT) {
        printf("Found * Operator\n");
        yylex();
        Factor();
        TermPrime();
    }
}

void Factor() {
    if(yytoken == LPAREN) {
        printf("Found (\n");
        yylex();
        Expression();
        if(yytoken == RPAREN) {
            printf("Found )\n");
            yylex();
        }
        else
            yyerror("Expected ')'");
    }
    else if(yytoken == NUM) {
        printf("Found number: %d\n", yytoken);
        result = yytoken;
        yylex();
    }
    else if(yytoken == ID) {
        printf("Found identifier\n");
        yylex();
    }
    else
        yyerror("Syntax error in factor");
}

void yyerror(char *s) {
    fprintf(stderr, "Error: %s\n", s);
    exit(1);
}

int main() {
    printf("Enter an expression (end with newline and Ctrl+D):\n");
    yylex();
    Expression();
    if(yytoken != 0)
        yyerror("Extra characters at end of input");
    printf("Result: %d\n", result);
    return 0;
}