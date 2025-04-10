%{
#include <stdio.h>
#include <stdlib.h>
#include <math.h>

void yyerror(char *error);
%}

%union {
    char fchar;
    double fval;
    int intval;
};

%token <intval>NUMBER
%token <fchar>NAME
%token SIN COS TAN
%type <fval>exp
%left '+' '-'
%left '*' '/'

%%
line: /* empty */
    | line stmt '\n'
    ;

stmt: NAME '=' exp { printf("=%f\n",$3); }
    | exp { printf("=%f\n",$1); }
    ;

exp : exp '+' exp { $$ = $1 + $3; }
    | exp '-' exp { $$ = $1 - $3; }
    | exp '*' exp { $$ = $1 * $3; }
    | exp '/' exp { 
        if($3==0) {
            printf("\nDivide by zero.");
            $$ = 0;
        } else {
            $$ = $1 / $3;
        }
    }
    | SIN '(' exp ')' { $$ = sin($3 * M_PI / 180.0); }
    | COS '(' exp ')' { $$ = cos($3 * M_PI / 180.0); }
    | TAN '(' exp ')' { $$ = tan($3 * M_PI / 180.0); }
    | '(' exp ')' { $$ = $2; }
    | NUMBER { $$ = $1; }
    ;
%%

void yyerror(char *error) {
    printf("%s\n",error);
}

int main() {
    yyparse();
    return 0;
}