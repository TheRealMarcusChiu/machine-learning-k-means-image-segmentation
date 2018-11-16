### Steps to run

install maven

mvn clean package
 
mvn exec:java -Dexec.mainClass="Main" -Dexec.args="Koala.jpg 2 Koala2.jpg"
 
### Report

Koala2.jpg 
- k=2, 1st run

Koala2-2.jpg 
- k=2, 2nd run

so on

Resulting images are different because of random initial means

Used Euclidean Distance of a 4D space: <alpha, red, green, blue>

Is there a tradeoff between image quality and degree of compression? 

the higher the value k, the better the image quality (usually)

What would be a good value of K for each of the two images?

I like k = 20

