<?php
    $host = "localhost";
    $user = "Francesco";
    $password_db = "root";
    $dbname = "geofind";

    $con = mysqli_connect($host, $user, $password_db, $dbname);
    
    $name = $_POST["nome"];
    $email = $_POST["email"];
    $password = $_POST["password"];
    
    $query = "select * from user where email like '".$email."';";
    $result = mysqli_query($con,$query);

    if(mysqli_num_rows($result)>0) {
        
        $response = array();
        $code = "reg_false";
        $message = "Email già esistente";
        array_push($response, array("code"=>$code, "message"=>$message));
        echo json_encode(array("server_response"=>$response));
    } 
    
    else {
    
        $statement = mysqli_prepare($con, "INSERT INTO user (nome, email, password) VALUES (?, ?, ?)");
        mysqli_stmt_bind_param($statement, "sss", $name, $email,        $password);
        mysqli_stmt_execute($statement);

    
        $response = array();
        $response["success"] = true;  
    
        echo json_encode($response);
        
    }
?>
