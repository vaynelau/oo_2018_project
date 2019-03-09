#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define MAX_SIZE 2000010

char str[MAX_SIZE];
int result[MAX_SIZE];
int visit[MAX_SIZE];

void printPoly();

int main(void)
{
    printf("#Please input the polynomial:\n");
    fgets(str,MAX_SIZE-1,stdin);

    int i;
    int state=0,sign=0; //sign保存多项式前的符号
    int num1=0,num2=0,flag1=0,flag2=0; //系数，次数及其符号位
    int bit1=0,bit2=0;  //对每一个输入数字的位数进行计数
    int count1=0,count2=0; //对多项式个数和每个多项式的项数进行计数

    for(i=0;i<strlen(str);i++){
        if(str[i]==' ') continue; //忽略空格
        switch(state){
            case 0:
                if(str[i]=='+'||str[i]=='-'){
                    if(str[i]=='-') sign=1;
                    else sign = 0;
                    state=1;
                }
                else if(str[i]=='{'){
                    sign = 0;
                    memset(visit,0,sizeof(int)*MAX_SIZE);
                    state = 2;
                }
                else {
                    printf("ERROR\n");
                    printf("#Incorrect input format.\n");
                    return 0;
                }
                break;

            case 1:
                if(str[i]=='{'){
                    if(count1>=20){
                        printf("ERROR\n");
                        printf("#Too many input polynomials.\n");
                        return 0;
                    }
                    memset(visit,0,sizeof(int)*MAX_SIZE);
                    count2=0;
                    state = 2;
                }
                else {
                    printf("ERROR\n");
                    printf("#Incorrect input format.\n");
                    return 0;
                }
                break;

            case 2:
                if(str[i]=='('){
                    num1=0;
                    num2=0;
                    bit1=0;
                    bit2=0;
                    state=3;
                }
                else {
                    printf("ERROR\n");
                    printf("#Incorrect input format.\n");
                    return 0;
                }
                break;

            case 3:
                if(str[i]>='0'&& str[i]<='9'){
                    flag1=0;
                    num1= num1*10+ str[i]-'0';
                    bit1++;
                    state=4;
                }
                else if(str[i]=='+' || str[i]=='-'){
                    if(str[i]=='-') flag1=1;
                    else flag1 = 0;
                    state=10;
                }
                else {
                    printf("ERROR\n");
                    printf("#Incorrect input format.\n");
                    return 0;
                }
                break;

            case 10:
                if(str[i]>='0'&& str[i]<='9'){
                    num1= num1*10+ str[i]-'0';
                    bit1++;
                    state=4;
                }
                else {
                    printf("ERROR\n");
                    printf("#Incorrect input format.\n");
                    return 0;
                }
                break;

            case 4:
                if(str[i]>='0'&& str[i]<='9'){
                    if(bit1>=6){
                        printf("ERROR\n");
                        printf("#Incorrect input format.\n");
                        return 0;
                    }
                    num1= num1*10+ str[i]-'0';
                    bit1++;
                    state=4;
                }
                else if(str[i]==','){
                    if(flag1==1) num1=-num1;
                    if(sign==1) num1=-num1;
                    state=5;
                }
                else {
                    printf("ERROR\n");
                    printf("#Incorrect input format.\n");
                    return 0;
                }
                break;

            case 5:
                if(str[i]>='0'&& str[i]<='9'){
                    num2= num2*10+ str[i]-'0';
                    bit2++;
                    flag2 = 0;
                    state=6;
                }
                else if(str[i]=='+' || str[i]=='-'){
                    if(str[i]=='-') flag2=1;
                    else flag2 = 0;
                    state=11;
                }
                else {
                    printf("ERROR\n");
                    printf("#Incorrect input format.\n");
                    return 0;
                }
                break;

            case 11:
                if(str[i]>='0'&& str[i]<='9'){
                    num2= num2*10+ str[i]-'0';
                    bit2++;
                    state=6;
                }
                else {
                    printf("ERROR\n");
                    printf("#Incorrect input format.\n");
                    return 0;
                }
                break;

            case 6:
                if(str[i]>='0'&& str[i]<='9'){
                    if(bit2>=6){
                        printf("ERROR\n");
                        printf("#Incorrect input format.\n");
                        return 0;
                    }
                    num2= num2*10+ str[i]-'0';
                    bit2++;
                    state=6;
                }
                else if(str[i]==')'){
                    if(flag2==1) num2=-num2;
                    if(num2<0){
                        printf("ERROR\n");
                        printf("#Incorrect input format.\n");
                        return 0;
                    }
                    if(visit[num2]==0){
                        result[num2]+=num1;
                        visit[num2]=1;
                        count2++;
                        state=7;
                    }
                    else{
                        printf("ERROR\n");
                        printf("#Similar items in the same polynomial.\n");
                        return 0;
                    }


                }
                else {
                    printf("ERROR\n");
                    printf("#Incorrect input format.\n");
                    return 0;
                }
                break;

            case 7:
                if(str[i]==','){
                    if(count2>=50){
                        printf("ERROR\n");
                        printf("#Too many input polynomial items.\n");
                        return 0;
                    }

                    state=2;
                }
                else if(str[i]=='}'){
                    count1++;
                    state=8;
                }
                else {
                    printf("ERROR\n");
                    printf("#Incorrect input format.\n");
                    return 0;
                }
                break;

            case 8:
                if(str[i]=='\n'){
                    printPoly();
                    return 0;
                }
                else if(str[i]=='+' || str[i]=='-'){
                    if(str[i]=='-') sign=1;
                    else sign = 0;
                    state=1;
                }
                else {
                    printf("ERROR\n");
                    printf("#Incorrect input format.\n");
                    return 0;
                }
                break;

        }
    }

    return 0;
}

void printPoly()
{
    int flag = 0,i;
    for(i=0;i<MAX_SIZE;i++) {
        if(result[i] != 0)
				flag =1;
    }
    if(flag == 0)
		printf("0");
    else {

        int flag2 = 0;
        printf("{");
        for(i=0;i< MAX_SIZE; i++) {
            if(result[i] != 0){
                if(flag2 == 0) {
                    printf("(%d,%d)",result[i],i);
                    flag2 = 1;
                }
                else {
                    printf(",(%d,%d)",result[i],i);
                }
            }

        }
        printf("}\n");
    }
    return;
}
