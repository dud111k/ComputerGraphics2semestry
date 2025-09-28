import pygame

pygame.init() # инициализация игры
screen = pygame.display.set_mode((800, 600)) #pygame.display.set_mode((800,600), flags = pygame.NOFRAME) --- без рамки, значение разрешения - кортеж
pygame.display.set_caption('OBJ CLASSROOM FIGHTING') # указание названия
icon = pygame.image.load('../imgs/icon.webp') # подгружаем иконочку
pygame.display.set_icon(icon) # ставим иконку
starting = True # флажок

square = pygame.Surface((200, 200)) # класс поверхность, экран - поверхность surface, размер
square.fill('Blue') # вместо кортежа rgb можно писать просто цвет
myfont = pygame.font.Font('../font/VT323-Regular.ttf', 50) # название шрифта, размер шрифта
text_surface = myfont.render('Vadim Kislov, OBJ Classroom Fighting', True, 'Green')
player1 = pygame.image.load('../imgs/main hero.png')
while starting: # пока активен цикл - экран горит
# screen нужен для любых махинаций с экраном

    pygame.draw.circle(screen, 'Green', (590, 300), 30) #(поверхность, цвет, координаты центра, ширина)
    screen.blit(square, (600, 300))  # blit - создает обьект на экране, сначала переменная потом координаты по x и y
    screen.blit(text_surface, (50, 200))
    screen.blit(player1, (50, 50))

# все координаты в идут от левого верхнего угла
    pygame.display.update() # нужно писать для обновления каждого кадра

    for event in pygame.event.get(): # выход из игры
        if event.type == pygame.QUIT:
            starting = False
            pygame.quit()


