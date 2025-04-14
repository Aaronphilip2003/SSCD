%{
#include <stdio.h>
#include <stdlib.h>

void yyerror(char *s);
int yylex(void);
%}

%token PLUS MULT LPAREN RPAREN NUM

%%
expr    : term 
        | expr PLUS term    
        ;

term    : factor
        | term MULT factor  
        ;

factor  : NUM              
        | LPAREN expr RPAREN
        ;
%%

void yyerror(char *s) {
    fprintf(stderr, "Error: %s\n", s);
    exit(1);
}

int main() {
    printf("Enter an expression (end with newline and Ctrl+D):\n");
    yyparse();
    printf("Parsing completed successfully!\n");
    return 0;
} 