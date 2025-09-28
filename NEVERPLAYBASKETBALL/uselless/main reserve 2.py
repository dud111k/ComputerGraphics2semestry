import pygame

pygame.init()

screen = pygame.display.set_mode((1920, 1080))
bg_sound = pygame.mixer.Sound('music/Metal Gear Rising_ Revengeance — It Has To Be This Way (8-bit Cover) (www.lightaudio.ru).mp3')
bg = pygame.image.load('imgs/bg.png').convert_alpha()  # движение за окном, шторы сделать позже
plane = pygame.image.load('imgs/plane.png').convert_alpha()
planes = []  # все выпущенные самолетики

game = True
gameover = pygame.font.Font('font/VT323-Regular.ttf', 100)
gameoverbgd = gameover.render('GAME OVER DEMIDOV', False, ('RED'))
gamerestart = gameover.render('PLAY BASKET AGAIN', False, ('GREEN'))
gamerestartrect = gamerestart.get_rect(topleft=(1200, 800))

walk = [
    pygame.image.load('imgs/tolyan.png').convert_alpha(),
    pygame.image.load('imgs/hero_right_shoot.png').convert_alpha(),
]
walk_right = pygame.image.load('imgs/tolyan__right.png').convert_alpha()
shooting = pygame.image.load('imgs/hero_right_shoot.png').convert_alpha()

enemy = pygame.image.load('imgs/Rusteim.png').convert_alpha()
enemy_x = 1500
enemy_y = 600
lebron = 0  # ФЛАГ ДЛЯ ДВИЖЕНИЯ РУСТАМА

plane_speed = 3
tolyan_speed = 1
tolyan_x = 150
tolyan_y = 150
tolyan_count = 0

bg_sound.play(-1)
starting = True
while starting:
    if game:
        delay = 3
        pygame.time.delay(delay)
        sprint = tolyan_speed * delay
        keyboards = pygame.key.get_pressed()
        screen.blit(bg, (0, 0))
        screen.blit(enemy, (enemy_x, enemy_y))


        tolyan_zone = walk_right.get_rect(topleft=(tolyan_x, tolyan_y))
        enemy_zone = enemy.get_rect(topleft=(enemy_x, enemy_y))

        if tolyan_zone.colliderect(enemy_zone):
            game = False
            print("GAME OVER DEMIDOV")
        if keyboards[pygame.K_RIGHT]:
            screen.blit(walk_right, (tolyan_x, tolyan_y))
        elif keyboards[pygame.K_SPACE]:
            screen.blit(shooting, (tolyan_x, tolyan_y))
        elif keyboards[pygame.K_ESCAPE]:
            pygame.quit()
        else:
            screen.blit(walk[0], (tolyan_x, tolyan_y))




        if enemy_y > 220 and lebron == 0:
            enemy_y -= 2
        if enemy_y < 240:
            lebron = 1
        if enemy_y < 850 and lebron == 1:
            enemy_y += 2
        if enemy_y > 849:
            lebron = 0

        if keyboards[pygame.K_b]:
            planes.append(plane.get_rect(topleft=(tolyan_x, tolyan_y)))
        if planes:
            for i in planes:
                screen.blit(plane, (i.x, i.y))
                i.x += 4



        if keyboards[pygame.K_LEFT] and tolyan_x > 100:
            tolyan_x -= sprint
            tolyan_count = 1
        elif keyboards[pygame.K_RIGHT]:
            tolyan_x += sprint
        elif keyboards[pygame.K_UP] and tolyan_y > 150:
            tolyan_y -= sprint
        elif keyboards[pygame.K_DOWN] and tolyan_y < 850:
            tolyan_y += sprint


    else:
        screen.fill('BLACK')
        screen.blit(gameoverbgd, (200, 400))
        screen.blit(gamerestart, gamerestartrect)

        mouse = pygame.mouse.get_pos()
        if gamerestartrect.collidepoint(mouse) and pygame.mouse.get_pressed()[0]:  # МЫШЬ (метод маус гет прессед - кортеж, 0 - ПКМ
            game = True                                                            # 1 - ЛКМ, 2 - КОЛЕСИКО )
            tolyan_x = 150
            tolyan_y = 150
            planes.clear()


    pygame.display.update()

    for event in pygame.event.get():
        if event.type == pygame.QUIT:
            starting = False
            pygame.quit()