#!/bin/bash

check_port() {
    local port=$1
    ss -tuln | grep -q ":$port"
}

get_pid() {
    local port=$1
    lsof -t -i :$port
}

kill_process() {
    local pid=$1
    sudo kill -9 $pid
}

port_8080_in_use=$(check_port 8080 && echo "yes" || echo "no")
port_4200_in_use=$(check_port 4200 && echo "yes" || echo "no")

echo "Port 8080 is in use: $port_8080_in_use"
echo "Port 4200 is in use: $port_4200_in_use"

if [ "$port_8080_in_use" == "yes" ] || [ "$port_4200_in_use" == "yes" ]; then
    echo "Do you want to release the occupied ports and continue? (y/n): "
    read -r user_choice
    if [ "$user_choice" != "y" ]; then
        echo "Operation aborted."
        exit 1
    fi

    if [ "$port_8080_in_use" == "yes" ]; then
        echo "Port 8080 is in use. Do you want to terminate the process using it? (y/n): "
        read -r user_choice
        if [ "$user_choice" == "y" ]; then
            pid=$(get_pid 8080)
            if [ -n "$pid" ]; then
                echo "Terminating process with PID: $pid"
                kill_process $pid
                sleep 2
            fi    
         else 
            echo "Operation aborted."
            exit 1    
        fi
    fi

    if [ "$port_4200_in_use" == "yes" ]; then
        echo "Port 4200 is in use. Do you want to terminate the process using it? (y/n): "
        read -r user_choice
        if [ "$user_choice" == "y" ]; then
            pid=$(get_pid 4200)
            if [ -n "$pid" ]; then
                echo "Terminating process with PID: $pid"
                kill_process $pid
                sleep 2
            fi
	else 
    	    echo "Operation aborted."
	    exit 1 
        fi
    fi
fi

if check_port 8080 || check_port 4200; then
    echo "Ports are still in use. Please check and resolve manually."
    exit 1
fi

docker compose up -d

sleep 10

cd backend
./mvnw spring-boot:run &

cd ../frontend
ng serve
