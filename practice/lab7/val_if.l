%{
    #include "y.tab.h"
%}

%%
"if"                            return IF;
"("                            return LPAREN;
")"                            return RPAREN;
"<"|">"|"<="|">="|"=="|"!="    return CMP;
"+"|"-"|"*"|"/"                return OPR;
"="                            return ASG;
([a-zA-Z])("_"|[a-zA-Z0-9])*   return ID;
[0-9]+                         return NUM;
";"                            return SC;
" "                            {}
%%

int yywrap()
{
    return 1;
}