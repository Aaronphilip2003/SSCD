%{
    #include<stdio.h>
    #include "y.tab.h"
    
    int yytoken;
%}

%%
"+" {yytoken = PLUS; return PLUS;}
"*" {yytoken = MULT; return MULT;}
"(" {yytoken = LPAREN; return LPAREN;}
")" {yytoken = RPAREN; return RPAREN;}
[0-9]+ {yytoken = NUM; return NUM;}
[a-zA-Z]+ {yytoken = ID; return ID;}
[ \t\n] ;
. {printf("Invalid Character\n");}
%%

int yywrap(){
    return 1;
}