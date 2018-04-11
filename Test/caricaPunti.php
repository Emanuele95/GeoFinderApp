<?php
    $host = "localhost";
    $user = "Francesco";
    $password = "root";
    $dbname = "geofind";

    $con = mysqli_connect($host, $user, $password, $dbname);

    $email = $_GET['email'];

    $query = "SELECT lat,lng,idUser,point.nomeLuogo FROM point, user WHERE user.email= '".$email."' AND point.idUser = user.id UNION SELECT lat,lng,idUser,point.nomeLuogo FROM point WHERE point.idUser = 1 ORDER BY idUser";
    

    $r = mysqli_query($con, $query);
    $result = array();

    if(mysqli_num_rows($r) > 0 ) {
        while($row = mysqli_fetch_array($r)){
            array_push($result,array(
            'lat'=>$row['lat'],
            'lng'=>$row['lng'],
            'nomeLuogo'=>$row['nomeLuogo'],
            'idUser'=>$row['idUser']       
                ));
        }
        
        echo json_encode($result);
        
    }

    mysqli_close($con);



?>