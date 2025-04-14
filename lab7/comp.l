%{
#include "y.tab.h"
%}

%%
"if"    return IF;
"while" return WHILE;
"for"   return FOR;
"("     return OP;
")"     return CP;
"<" |
">" |
"<=" |
">=" |
"==" |
"!="    return CMP;
"+" |
"-" |
"*" |
"/"     return OPR;
"="     return ASG;
([a-zA-Z])("_"|[a-zA-Z0-9])* return ID;
[0-9]+  return NUM;
";"     return SC;
" "     {}

%%

int yywrap()
{
    return 1;
}
