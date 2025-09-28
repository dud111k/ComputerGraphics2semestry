import pygame
import time

pygame.init()


neverplaybasket = False
height = 720
width = 1280
screen = pygame.display.set_mode((width, height))
bg_sound = pygame.mixer.Sound('music/Metal Gear Rising_ Revengeance — It Has To Be This Way (8-bit Cover) (www.lightaudio.ru).mp3')
three_sound = pygame.mixer.Sound('imgs/rustam_sounds/ochkoviy.ogg')
megadunk_sound = pygame.mixer.Sound('imgs/rustam_sounds/megadunk.ogg')
gameover_sound = pygame.mixer.Sound('imgs/rustam_sounds/gameover_01.ogg')
plane_sound = pygame.mixer.Sound('imgs/rustam_sounds/samolet.mp3')

bg_gameover = pygame.image.load('imgs/bg_gameover.png').convert_alpha()
bg = pygame.image.load('imgs/bg.png').convert_alpha()  # движение за окном, шторы сделать позже
bg_win = pygame.image.load('imgs/bg_win.png').convert_alpha()
plane = pygame.image.load('imgs/plane.png').convert_alpha()
planes = []  # все выпущенные самолетики
bar = pygame.image.load('imgs/hp.png').convert_alpha()
red_bar = pygame.image.load('imgs/hp_red.png').convert_alpha()

menu = True
game = True
gameover = pygame.font.Font('font/VT323-Regular.ttf', 100)
gameoverbgd = gameover.render('GAME OVER', False, ('RED'))
gamerestart = gameover.render('PLAY BASKET AGAIN', False, ('GREEN'))
gamewin = gameover.render('YOU WIN', False, ('GREEN'))
gamereswin = gameover.render('NEVER PLAY BASKET AGAIN!', False, ('RED'))
gamereswinrect = gamereswin.get_rect(topleft=(200, 850))
gamerestartrect = gamerestart.get_rect(topleft=(1200, 800))

walk = [
    pygame.image.load('imgs/tolyan.png').convert_alpha(),
    pygame.image.load('imgs/hero_right_shoot.png').convert_alpha(),
]
walk_right = pygame.image.load('imgs/tolyan__right.png').convert_alpha()
shooting = pygame.image.load('imgs/hero_right_shoot.png').convert_alpha()

cd_shoot = 0
ball = pygame.image.load('imgs/baller.png').convert_alpha()
balls = []
enemy_lives = 12
enemy_speed = 2
enemy = pygame.image.load('imgs/Rusteim.png').convert_alpha()
enemy_x = 1500
enemy_y = 600
lebron = 0  # ФЛАГ ДЛЯ ДВИЖЕНИЯ РУСТАМА

health_tolyan = 3
plane_speed = 2
tolyan_speed = 1
tolyan_x = 150
tolyan_y = 150
tolyan_count = 0

bg_sound.play(-1)

starting = True
while starting:
    if game:
        enemy_speed = 2 + (12 - enemy_lives) / 2
        delay = 3
        pygame.time.delay(delay)
        sprint = tolyan_speed * delay
        plane_sprint = plane_speed * delay
        keyboards = pygame.key.get_pressed()
        screen.blit(bg, (0, 0))
        screen.blit(enemy, (enemy_x, enemy_y))

        tolyan_zone = walk_right.get_rect(topleft=(tolyan_x, tolyan_y))
        enemy_zone = enemy.get_rect(topleft=(enemy_x, enemy_y))




        if keyboards[pygame.K_RIGHT]:
            screen.blit(walk_right, (tolyan_x, tolyan_y))
        elif keyboards[pygame.K_SPACE]:
            screen.blit(shooting, (tolyan_x, tolyan_y))
        elif keyboards[pygame.K_ESCAPE]:
            pygame.quit()
        else:
            screen.blit(walk[0], (tolyan_x, tolyan_y))




        if enemy_y > 220 and lebron == 0:
            enemy_y -= enemy_speed
        if enemy_y < 240:
            lebron = 1
        if enemy_y < 850 and lebron == 1:
            enemy_y += enemy_speed
        if enemy_y > 849:
            lebron = 0


        if cd_shoot == 1:
            balls.append(ball.get_rect(topleft=(enemy_x - 50, enemy_y - 50)))
            cd_shoot = 0
        else:
            if enemy_lives > 8 and (round((pygame.time.get_ticks() / 1000), 1)) % 4 == 0:
                cd_shoot = 1
            elif enemy_lives >= 6 and (round((pygame.time.get_ticks() / 1000), 1)) % 3 == 0:
                cd_shoot = 1
            elif enemy_lives >= 3 and (round((pygame.time.get_ticks() / 1000), 1)) % 2 == 0:
                cd_shoot = 1
            elif enemy_lives < 3 and (round((pygame.time.get_ticks() / 1000), 1)) % 0.5 == 0:
                cd_shoot = 1

        if health_tolyan == 0:
            gameover_sound.play(0)

        if balls:
            for (j, elem) in enumerate(balls):
                screen.blit(ball, (elem.x, elem.y))
                elem.x -= 5
                if len(balls) > 1:
                    del balls[-1]

                if elem.x < 0:
                    balls.pop(j)

                if elem.colliderect(tolyan_zone) and health_tolyan == 3:
                    megadunk_sound.play()
                    health_tolyan -= 1
                    balls.pop(j)
                elif elem.colliderect(tolyan_zone) and health_tolyan == 2:
                    three_sound.play()
                    health_tolyan -= 1
                    balls.pop(j)
                elif elem.colliderect(tolyan_zone) and health_tolyan == 1:
                    gameover_sound.play(0)
                    game = False







        if keyboards[pygame.K_SPACE]:
            planes.append(plane.get_rect(topleft=(tolyan_x + 120, tolyan_y + 100)))
        if planes:
            for (i, el) in enumerate(planes):
                plane_sound.play(0)
                screen.blit(plane, (el.x, el.y))
                el.x += plane_sprint
                if len(planes) > 1:
                    del planes[-1]

                if el.x > 1920:
                    planes.pop(i)

                if el.colliderect(enemy_zone):
                    enemy_lives -= 1
                    del planes[-1]
                if enemy_lives == 0:
                    print("WIN")
                    game = False




        if keyboards[pygame.K_LEFT] and tolyan_x > 100:
            tolyan_x -= sprint
            tolyan_count = 1
        elif keyboards[pygame.K_RIGHT] and tolyan_x < 1000:
            tolyan_x += sprint
        elif keyboards[pygame.K_UP] and tolyan_y > 150:
            tolyan_y -= sprint
        elif keyboards[pygame.K_DOWN] and tolyan_y < 850:
            tolyan_y += sprint




        screen.blit(bar, (30, 30))
        screen.blit(bar, (100, 30))
        screen.blit(bar, (170, 30))

        if health_tolyan < 3:
            screen.blit(red_bar, (170, 30))
        if health_tolyan < 2:
            screen.blit(red_bar, (100, 30))
        rustik_text = gameover.render("ENEMY LIVES LEFT:", False, ('RED'))
        rustik_lifes = gameover.render(str(enemy_lives), False, ('GREEN'))



    elif enemy_lives == 0:
        screen.blit(bg_win, (0, 0))
        screen.blit(gamereswin,gamereswinrect)
        screen.blit(gamewin, (200, 400))
        health_tolyan = 1


        mouse = pygame.mouse.get_pos()
        if gamereswinrect.collidepoint(mouse) and pygame.mouse.get_pressed()[0]:  # МЫШЬ (метод маус гет прессед - кортеж, 0 - ПКМ
            game = True                                                         # 1 - ЛКМ, 2 - КОЛЕСИКО )
            tolyan_x = 150
            screen.blit(bar, (100, 30))
            screen.blit(bar, (170, 30))
            tolyan_y = 150
            planes.clear()
            enemy_lives = 12
            enemy_x = 1500
            enemy_y = 600
            if balls:
                balls.pop()
            neverplaybasket = True



    else:
        if neverplaybasket:
            pygame.quit()
        screen.blit(bg_gameover, (0, 0))
        screen.blit(gameoverbgd, (200, 400))
        screen.blit(gamerestart, gamerestartrect)

        screen.blit(rustik_text, (200, 600))
        screen.blit(rustik_lifes, (900, 600))
        health_tolyan = 3




        mouse = pygame.mouse.get_pos()
        if gamerestartrect.collidepoint(mouse) and pygame.mouse.get_pressed()[0]:  # МЫШЬ (метод маус гет прессед - кортеж, 0 - ПКМ
            game = True                                                         # 1 - ЛКМ, 2 - КОЛЕСИКО )
            tolyan_x = 150
            screen.blit(bar, (100, 30))
            screen.blit(bar, (170, 30))
            tolyan_y = 150
            planes.clear()
            enemy_lives = 12
            enemy_x = 1500
            enemy_y = 600
            balls.pop()




    pygame.display.update()

    for event in pygame.event.get():
        if event.type == pygame.QUIT:
            starting = False
            pygame.quit()