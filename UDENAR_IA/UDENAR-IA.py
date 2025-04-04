from pathlib import Path
import socket
import numpy as np
import cv2
import tensorflow as tf

SERVER_IP = "127.0.0.1"
SERVER_PORT = 5000  # Puerto en el que el servidor está escuchando
MODEL_PATH = "C:/Users/Public/conv.h5" 

def load_model():
    modelo = tf.keras.models.load_model(MODEL_PATH)
    return modelo

def predict_img(modelo, imagen):
    imagen_preprocesada = preprocess_image(imagen)
    predicciones = modelo.predict(np.array([imagen_preprocesada]))
    clase_predicha = np.argmax(predicciones)
    print(f"clase_predicha :{clase_predicha}")  
    return predicciones, clase_predicha

def preprocess_image(imagen):
    imagen_p = cv2.resize(imagen, (28, 28))
    imagen_p = imagen_p.reshape((28, 28, 1)) 
    return imagen_p

def length_bytes(bytes):
    if bytes <= 0xFF:
        byte_length = 1
    elif bytes <= 0xFFFF:
        byte_length = 2
    elif bytes <= 0xFFFFFFFF:
        byte_length = 4
    else:
        byte_length = 8
    return byte_length

def Bytesimages(image_path):
    lista_len_bytes = []
    for path in image_path:
        path = path.strip()
        ruta_archivo_str = str(path)
        print(f"File paths: {ruta_archivo_str}")
        try:
            with open(ruta_archivo_str, 'rb') as file:
                ruta_archivo_bytes = file.read()
            lista_len_bytes.append(ruta_archivo_bytes) 
        except FileNotFoundError:
           print("error")
        
    return lista_len_bytes

def receive_data(sock):
    length_bytes = sock.recv(4)
    length = int.from_bytes(length_bytes, byteorder='big')

    data = b''
    while len(data) < length:
        packet = sock.recv(length - len(data))
        if not packet:
            return None
        data += packet

    decoded_data = data.decode('utf-8')
    return decoded_data

def main():
    modelo = load_model()
   
    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as server_socket:
        server_socket.bind((SERVER_IP, SERVER_PORT))
        server_socket.listen()
        while True:
            print(f"Servidor escuchando en {SERVER_IP}:{SERVER_PORT}")  
            connection, client_address = server_socket.accept()
            try:
                print(f"Conexión establecida desde {client_address}")           
                image_arrays = receive_data(connection)
                print(f"tamaño de Bytesimages image_arrays  {len(image_arrays)}")  
                image_path = image_arrays.split(',')
                result = Bytesimages(image_path)
                print(f"tamaño de Bytesimages  {len(Bytesimages(image_path))}") 
                if result is None or not result:
                    connection.sendall(int(9999).to_bytes(4, byteorder='big'))
                    print("No se encontraron archivos, enviando 9999 al cliente.")
                    continue
                
                for img in Bytesimages(image_path):
                    print(f"total_data: {img[:20]}..")
                    print(f"Longitud de los bytes de la imagen recibidos: {len(img)}")
                    nparr = np.frombuffer(img, np.uint8)
                    img_np = cv2.imdecode(nparr, cv2.IMREAD_GRAYSCALE) 
                    img_np = preprocess_image(img_np)

                    if img_np is None or img_np.size == 0:
                        print("Error: La imagen no se pudo decodificar o está vacía.")
                    else:
                        longitud_images = len(img)
                        byte_length = length_bytes(longitud_images)
                        predicciones, clase = predict_img(modelo, img_np)
                        print("Predicciones: ", predicciones, " clase: ", clase)
                        print("byte_length: ", byte_length, " longitud_images: ", longitud_images)
                        connection.sendall(int(clase).to_bytes(4, byteorder='big'))
                        print("Clase enviada:", clase)
                
            except Exception as e:
                print(f"Ocurrió un error: {e}")
            finally:
                connection.close()

if __name__ == "__main__":
    main()
