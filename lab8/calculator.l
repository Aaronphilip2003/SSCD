%{
#include "y.tab.h"
#include <stdio.h>
%}

%%
[0-9]+      { printf("NUMBER: %s\n", yytext); yylval.intval = atoi(yytext); return NUMBER; }
"sin"       { printf("SIN\n"); return SIN; }
"cos"       { printf("COS\n"); return COS; }
"tan"       { printf("TAN\n"); return TAN; }
"("         { printf("LPAREN\n"); return '('; }
")"         { printf("RPAREN\n"); return ')'; }
[a-zA-Z]+   { printf("NAME: %s\n", yytext); yylval.fchar = yytext[0]; return NAME; }
[-+*/=\n]   { printf("OP: %c\n", *yytext); return *yytext; }
[ \t]       ;
.           { printf("Invalid character: %s\n", yytext); }
%%

int yywrap() {
    return 1;
}