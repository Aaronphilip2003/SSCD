%{
int c= 0, w = 0, l = 0;
%}
%%
[ ] {printf("%sword\n",yytext);c++;w++;}
[\n] {printf("%sline\n", yytext);c++;w++;l++;}
. {printf("%sletter\n",yytext);c++;}
%%
int yywrap(){}
int main(){
yylex();	
printf("\nNumber of characters = %d\t words=%d\t lines=%d in the given input", c,w,l);
return 0;
}

