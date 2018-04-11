<?php 

$host = "localhost";
$user = "Francesco";
$password = "root";
$dbname = "geofind";


$con = mysqli_connect($host, $user, $password, $dbname);

if(!$con) {

	die("Errore nella connessione del database". mysql_connect_error());

} else {

	echo "<h3> Connessione al database avvenuta</h3>";

}
