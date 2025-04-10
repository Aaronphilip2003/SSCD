%{
#include <stdio.h>
#include "y.tab.h"

int yytoken;  /* Add this declaration */
%}

%%
"+"     { yytoken = PLUS; return PLUS; }
"*"     { yytoken = MULT; return MULT; }
"("     { yytoken = LPAREN; return LPAREN; }
")"     { yytoken = RPAREN; return RPAREN; }
[0-9]+  { yytoken = NUM; return NUM; }
[ \t\n] ; /* Skip whitespace */
.       { printf("Invalid character\n"); }
%%

int yywrap() {
    return 1;
} 