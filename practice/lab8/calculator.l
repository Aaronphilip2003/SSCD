%{
    #include<stdio.h>
    #include "y.tab.h"
%}

%%
[0-9]+ {printf("Number %s\n",yytext); yylval.intval=atoi(yytext); return NUMBER}
"(" {printf("LPAREN\n"); return '(';}
")" {printf("RPAREN\n"); return ')';}
[-+*/=\n] { printf("OP: %c\n", *yytext); return *yytext; }
%%

int yywrap()
{
    return 1;
}