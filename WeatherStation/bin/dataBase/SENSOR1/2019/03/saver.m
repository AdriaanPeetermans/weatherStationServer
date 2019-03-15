f = fopen('03_.txt','w');
for i = 1 : 143
   fprintf(f,num2str(Light(i),3));
   fprintf(f,',');
   fprintf(f,num2str(Moisture(i),4));
   fprintf(f,',');
   fprintf(f,num2str(Temperature(i),4));
   fprintf(f,',');
   fprintf(f,num2str(Pressure(i),6));
   fprintf(f,',');
   fprintf(f,num2str(PP(i), 3));
   fprintf(f,',');
   fprintf(f,num2str(BP(i), 3));
   fprintf(f,' ');
   fprintf(f,datestr(Hour(i),'HH:MM:ss'));
   fprintf(f,' ');
   fprintf(f,datestr(Date(i),'dd/mm/yy'));
   fprintf(f, '\n');
end
fclose(f);